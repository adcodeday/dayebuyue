package org.lu.zhaodazi.websocket.service;

import io.netty.channel.Channel;
import org.lu.zhaodazi.auth.authtication.WxAuthenticationToken;
import org.lu.zhaodazi.user.domain.entity.TokenInfo;
import org.springframework.stereotype.Service;


public interface WebsocketService {

    void removed(Channel channel) throws Exception;

    void connect(Channel channel);

//    void authorize(Channel channel, WSAuthorize wsAuthorize);

    void handleLoginReq(Channel channel);
    void startMatch(Channel channel) throws Exception;
    void authorize(Channel channel);

    void sendMessage(Channel channel, String data) throws Exception;
}
