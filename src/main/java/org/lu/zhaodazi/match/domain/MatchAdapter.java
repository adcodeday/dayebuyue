package org.lu.zhaodazi.match.domain;

import cn.hutool.extra.spring.SpringUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import org.lu.zhaodazi.match.domain.dto.Match;
import org.lu.zhaodazi.match.domain.entity.MatchingCondition;
import org.lu.zhaodazi.user.domain.entity.User;
import org.lu.zhaodazi.user.service.Impl.UserServiceImpl;
import org.lu.zhaodazi.user.service.UserService;
import org.lu.zhaodazi.websocket.NettyUtil;
import org.lu.zhaodazi.websocket.NettyWebsocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MatchAdapter {
    @Autowired
    public  UserService userService;
    public Match buildMatch(String channelId){
        Channel channel = NettyWebsocketServer.allChannels.get(channelId);
        User user = userService.loadUserByUserId(NettyUtil.getAttr(channel,NettyUtil.UID));
        return new Match(new MatchingCondition(user.getSex(),user.getProvince(),user.getWantSex(),user.getWantProvince(),user.getCodeWord()),channelId);
    }
}
