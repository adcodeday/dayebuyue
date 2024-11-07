package org.lu.zhaodazi.websocket;


import cn.hutool.core.net.url.UrlBuilder;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;
import org.apache.commons.lang3.StringUtils;
import org.lu.zhaodazi.user.domain.entity.TokenInfo;

import java.net.InetSocketAddress;
import java.util.Optional;
//这个类的作用是处理HTTP请求，提取请求中的token和IP，并将它们设置为通道的属性
public class HttpHeadersHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        // 检查消息是否是HTTP请求
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest request = (FullHttpRequest) msg;
            UrlBuilder urlBuilder = UrlBuilder.ofHttp(request.uri());

            // 从URL查询参数中提取token
            String uuid = Optional.ofNullable(urlBuilder.getQuery()).map(k->k.get("token")).map(CharSequence::toString).orElse("");
            Long uid = Optional.ofNullable(urlBuilder.getQuery()).map(k->k.get("uid")).map(Object::toString).map(Long::parseLong).orElse(0L);
            if(uid!=0 && !uuid.isEmpty()){
                TokenInfo tokenInfo=new TokenInfo(uid,uuid);
                NettyUtil.setAttr(ctx.channel(), NettyUtil.TOKEN_INFO, tokenInfo);
            }
            // 更新请求的URI，只保留路径部分
            request.setUri(urlBuilder.getPath().toString());

            HttpHeaders headers = request.headers();
            String ip = headers.get("X-Real-IP");
            if (StringUtils.isEmpty(ip)) {
                InetSocketAddress address = (InetSocketAddress) ctx.channel().remoteAddress();
                ip = address.getAddress().getHostAddress();
            }
            NettyUtil.setAttr(ctx.channel(), NettyUtil.IP, ip);

            ctx.pipeline().remove(this);

            ctx.fireChannelRead(request);
        } else {
            ctx.fireChannelRead(msg);
        }
    }
}