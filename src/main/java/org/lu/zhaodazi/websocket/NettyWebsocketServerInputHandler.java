package org.lu.zhaodazi.websocket;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.lu.zhaodazi.user.domain.entity.TokenInfo;
import org.lu.zhaodazi.websocket.domain.WSBaseReq;
import org.lu.zhaodazi.websocket.enums.ChannelStatesEnum;
import org.lu.zhaodazi.websocket.enums.WSReqTypeEnum;
import org.lu.zhaodazi.websocket.service.WebsocketService;
import org.lu.zhaodazi.websocket.service.impl.WebsocketServiceImpl;

import java.util.concurrent.ConcurrentHashMap;

/**
 * WebSocket 服务器处理器
 * 处理 WebSocket 连接、消息和事件
 */
@Slf4j
@Sharable
//对消息进行路由，对channelGroup进行管理，对channel事件进行处理
public class NettyWebsocketServerInputHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    public WebsocketService websocketService= new WebsocketServiceImpl();
    private final ConcurrentHashMap<String,Channel> channelGroup;

    public NettyWebsocketServerInputHandler(ConcurrentHashMap<String, Channel> channelGroup) {
        this.channelGroup = channelGroup;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 获取WebSocketService实例
        this.websocketService = getService();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        userOffLine(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        userOffLine(ctx);
    }

    private void userOffLine(ChannelHandlerContext ctx) throws Exception {
        // 调用WebSocketService的removed方法，处理用户离线逻辑
        this.websocketService.removed(ctx.channel());
        //移除channelId和channel对应关系
        channelGroup.remove(ctx.channel().id().asShortText());
        // 关闭通道
        ctx.channel().close();
    }

    /**
     * 处理用户事件
     * 包括心跳检查和WebSocket握手完成事件
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲事件，关闭用户连接
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                userOffLine(ctx);
            }
        } else if (evt instanceof WebSocketServerProtocolHandler.HandshakeComplete) {
            // WebSocket握手完成，处理连接和授权
            this.websocketService.connect(ctx.channel());
            this.websocketService.authorize(ctx.channel());
        }
        // 调用父类的userEventTriggered方法
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 处理异常
     * 记录异常日志并关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 记录警告日志，包含异常信息
        log.warn("异常发生，异常消息 ={}", cause);
        userOffLine(ctx);
        // 关闭通道
        ctx.channel().close();
    }

    /**
     * 获取WebSocketService实例
     * @return WebSocketService实例
     */
    private WebsocketService getService() {
        // 使用SpringUtil从Spring上下文中获取WebSocketService实例
        return SpringUtil.getBean(WebsocketService.class);
    }

    /**
     * 读取并处理客户端发送的WebSocket消息
     * @param ctx ChannelHandlerContext
     * @param msg 客户端发送的TextWebSocketFrame消息
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        System.out.println(msg.text());
        WSBaseReq wsBaseReq = JSONUtil.toBean(msg.text(), WSBaseReq.class);
        WSReqTypeEnum wsReqTypeEnum = WSReqTypeEnum.of(wsBaseReq.getType());
        switch (wsReqTypeEnum) {
            case LOGIN:
                this.websocketService.handleLoginReq(ctx.channel());
                break;
            case STOP:
                this.websocketService.removed(ctx.channel());
                break;
            case START_MATCH:
                this.websocketService.startMatch(ctx.channel());
                break;
            case MESSAGE:
                this.websocketService.sendMessage(ctx.channel(),wsBaseReq.getData());
            case HEARTBEAT:
                log.info("接收心跳包");
                break;
            default:
                log.info("未知类型");
        }
    }
    //TODO 频控有两种情况，第一种是对websocket连接发送的消息进行频控，第二种是对http调用接口的频率进行频控
    //TODO 两个注解接口---单一频控，组合频控，两个切面类--websocket频控，http频控，三个策略----固定窗口，滑动窗口，令牌桶
}

