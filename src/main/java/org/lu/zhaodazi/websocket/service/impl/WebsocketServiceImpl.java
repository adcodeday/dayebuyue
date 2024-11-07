package org.lu.zhaodazi.websocket.service.impl;

import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.lu.zhaodazi.auth.authtication.WxAuthenticationToken;
import org.lu.zhaodazi.auth.service.AuthService;
import org.lu.zhaodazi.auth.service.impl.AuthServiceImpl;
import org.lu.zhaodazi.common.config.RabbitMQConfig;
import org.lu.zhaodazi.common.constant.RedisKey;
import org.lu.zhaodazi.common.domain.vo.entity.CustomMessage;
import org.lu.zhaodazi.common.util.RedisUtil;
import org.lu.zhaodazi.user.domain.entity.TokenInfo;
import org.lu.zhaodazi.user.domain.entity.User;
import org.lu.zhaodazi.user.service.Impl.TokenServiceImpl;
import org.lu.zhaodazi.user.service.TokenService;
import org.lu.zhaodazi.websocket.NettyUtil;
import org.lu.zhaodazi.websocket.NettyWebsocketServer;
import org.lu.zhaodazi.websocket.domain.WSBaseResp;
import org.lu.zhaodazi.websocket.enums.ChannelStatesEnum;
import org.lu.zhaodazi.websocket.enums.WSRespTypeEnum;
import org.lu.zhaodazi.websocket.service.WebsocketService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
//业务逻辑（处理业务，接收消息，发送消息）以及对通道属性的维护
public class WebsocketServiceImpl implements WebsocketService {
    @Autowired
    public TokenService tokenService;
    @Autowired
    public AuthService authService;
    @Autowired
    RabbitTemplate rabbitTemplate;

    private static final String LOGIN_CODE = "loginCode";

    private static final Duration EXPIRE_TIME = Duration.ofMinutes(5);
    private static final Long MAX_MUM_SIZE = 10000L;
    /**
     * 所有请求登录的code与channel关系
     */
    public static final Cache<Integer, Channel> WAIT_LOGIN_MAP1 = Caffeine.newBuilder()
            .expireAfterWrite(EXPIRE_TIME)
            .maximumSize(MAX_MUM_SIZE)
            .build();

    @Override
    public void removed(Channel channel) throws Exception {
        //两个地方会用到，无论是前后端处理连接关闭还是接收取消匹配和离开对话消息,处理的是业务逻辑，并不处理channel关闭以及channelgroup清除
        ChannelStatesEnum state = NettyUtil.getAttr(channel, NettyUtil.STATE);
        switch (state) {
            case AUTHENTICATED:
                break;
            case MATCHING:
                NettyUtil.changeState(channel, NettyUtil.STATE, state, ChannelStatesEnum.AUTHENTICATED);
                rabbitTemplate.convertAndSend(RabbitMQConfig.MATCH_EX, RabbitMQConfig.MATCH_START_BIND, CustomMessage.cancelMatch(channel.id().asShortText()));
                break;
            case CHATTING:
                NettyUtil.changeState(channel, NettyUtil.STATE, state, ChannelStatesEnum.AUTHENTICATED);
                leave(channel);
                break;
            case UNAUTHENTICATED:
                Integer value = NettyUtil.getAttr(channel, AttributeKey.valueOf("LOGIN_CODE"));
                WAIT_LOGIN_MAP1.invalidate(value);
                break;
            default:
                log.info("未知状态");
        }

    }

    private void leave(Channel channel) {
        Channel channel1 = NettyUtil.getAttr(channel, NettyUtil.TALKING_TO);
        NettyUtil.setAttr(channel, NettyUtil.TALKING_TO, null);
        NettyUtil.setAttr(channel, NettyUtil.STATE, ChannelStatesEnum.AUTHENTICATED);
        NettyUtil.setAttr(channel1, NettyUtil.TALKING_TO, null);
        NettyUtil.setAttr(channel1, NettyUtil.STATE, ChannelStatesEnum.AUTHENTICATED);
        sendMsg(channel1,WSRespTypeEnum.LEAVE);
    }

    //TODO 做频控
    @Override
    public void connect(Channel channel) {

    }

    @Override
    public void authorize(Channel channel) {

        TokenInfo tokenInfo = NettyUtil.getAttr(channel, NettyUtil.TOKEN_INFO);
        if (tokenInfo == null) {
            NettyUtil.setAttr(channel, NettyUtil.STATE, ChannelStatesEnum.UNAUTHENTICATED);
            return;
        }
        // 如果token不为空，进行授权
        if (authService.verify(tokenInfo)) {
            NettyUtil.setAttr(channel, NettyUtil.STATE, ChannelStatesEnum.AUTHENTICATED);
            NettyUtil.setAttr(channel, NettyUtil.UID, tokenInfo.getUid());
        } else {
            sendMsg(channel, WSRespTypeEnum.AUTH_FAIL);
        }
    }

    @Override
    public void sendMessage(Channel channel, String message) throws Exception {
        if (!NettyUtil.getAttr(channel,NettyUtil.STATE).equals(ChannelStatesEnum.CHATTING)){
            throw new Exception("不在聊天状态");
        }
        Channel channel1 = NettyUtil.getAttr(channel, NettyUtil.TALKING_TO);


        int length = message.length();
        String asterisksString = "";
        for (int i = 0; i < length; i++) {
            asterisksString += "*";
        }



        sendMsg(channel1,new WSBaseResp<>(6,asterisksString));
    }

    //TODO 频控
    @Override
    public void handleLoginReq(Channel channel) {
        Integer code = generateLoginCode(channel);
        sendMsg(channel, new WSBaseResp<>(1, code));
    }


    public static boolean exist(Integer code) {
        Channel channel = WAIT_LOGIN_MAP1.getIfPresent(code);
        if (!Objects.isNull(channel)) {
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
    public static boolean loginSuccess(WxAuthenticationToken authenticated, Integer code) {
        Channel channel = WAIT_LOGIN_MAP1.getIfPresent(code);
        if (Objects.isNull(channel)) {
            return Boolean.FALSE;
        }
        User user = (User) authenticated.getPrincipal();
        TokenInfo tokenInfo = TokenServiceImpl.generate(user);
        sendMsg(channel, new WSBaseResp<>(2, tokenInfo));
        //TODO 发消息解析IP地址
        WAIT_LOGIN_MAP1.invalidate(code);
        return Boolean.TRUE;

    }

    //TODO 频控
    public void startMatch(Channel channel) throws Exception {
//        if(NettyUtil.getAttr(channel, NettyUtil.STATE)!=ChannelStatesEnum.AUTHENTICATED){
//            sendMsg(channel,WSRespTypeEnum.STATE_ERROR);
//        }
        NettyUtil.changeState(channel, NettyUtil.STATE, ChannelStatesEnum.AUTHENTICATED, ChannelStatesEnum.MATCHING);
        rabbitTemplate.convertAndSend(RabbitMQConfig.MATCH_EX, RabbitMQConfig.MATCH_START_BIND, CustomMessage.startMatch(channel.id().asShortText()));
        //TODO 失败处理,全局异常处理
    }

    private static Integer generateLoginCode(Channel channel) {
        int inc;
        do {
            inc = RedisUtil.integerInc(RedisKey.getKey(LOGIN_CODE), (int) EXPIRE_TIME.toMinutes(), TimeUnit.MINUTES);
        } while (WAIT_LOGIN_MAP1.asMap().containsKey(inc));
        NettyUtil.setAttr(channel, AttributeKey.valueOf("LOGIN_CODE"), inc);
        WAIT_LOGIN_MAP1.put(inc, channel);
        return inc;
    }

    private static <T> void sendMsg(Channel channel, WSBaseResp<T> res) {
        channel.writeAndFlush(new TextWebSocketFrame(JSONUtil.toJsonStr(res)));
    }

    private static void sendMsg(Channel channel, WSRespTypeEnum res) {
        WSBaseResp<String> stringWSBaseResp = new WSBaseResp<>(res.getType(), res.getDesc());
        sendMsg(channel, stringWSBaseResp);
    }

    //TODO 接收匹配成功的消息，建立连接，可以AK里写对方channel

    @RabbitListener(queues = RabbitMQConfig.MATCH_FINISH_QUEUE)
    public void getMessage(CustomMessage message) throws Exception {
        if (message == null) {
            return;
        }
        if (message.getType().equals(CustomMessage.MATCH_SUC)) {
            //匹配成功逻辑
            ArrayList<String> channelIds = (ArrayList) message.getData();
            Channel[] channels = new Channel[2];
            for (int i = 0; i < channelIds.size(); i++) {
                Channel channel = NettyWebsocketServer.allChannels.get(channelIds.get(i));
                channels[i] = channel;
                NettyUtil.changeState(channel, NettyUtil.STATE, ChannelStatesEnum.MATCHING, ChannelStatesEnum.CHATTING);
                sendMsg(channel, WSRespTypeEnum.MATCH_SUC);
            }
            NettyUtil.setAttr(channels[0], NettyUtil.TALKING_TO, channels[1]);
            NettyUtil.setAttr(channels[1], NettyUtil.TALKING_TO, channels[0]);
        } else if (message.getType().equals(CustomMessage.MATCH_FAIL)) {
            //匹配失败逻辑
            String channelId = (String) message.getData();
            Channel channel = NettyWebsocketServer.allChannels.get(channelId);
            NettyUtil.changeState(channel, NettyUtil.STATE, ChannelStatesEnum.MATCHING, ChannelStatesEnum.AUTHENTICATED);
        }
    }
}
