package org.lu.zhaodazi.websocket;


import cn.hutool.json.JSONUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelId;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import org.lu.zhaodazi.user.domain.entity.TokenInfo;
import org.lu.zhaodazi.websocket.enums.ChannelStatesEnum;

/**
 * Description: netty工具类
 * Author: <a href="https://github.com/zongzibinbin">abin</a>
 * Date: 2023-04-18
 */
//这个类
public class NettyUtil {
    public static AttributeKey<TokenInfo> TOKEN_INFO = AttributeKey.valueOf("tokenInfo");
    public static AttributeKey<String> IP = AttributeKey.valueOf("ip");
    public static AttributeKey<Long> UID = AttributeKey.valueOf("uid");

    // 定义用于存储 WebSocket 握手器的 AttributeKey
    // WebSocketServerHandshaker 用于完成 WebSocket 的握手过程
    // 存储这个对象可以在需要时方便地完成 WebSocket 的关闭等操作
    public static AttributeKey<WebSocketServerHandshaker> HANDSHAKER_ATTR_KEY = AttributeKey.valueOf(WebSocketServerHandshaker.class, "HANDSHAKER");
    public static AttributeKey<ChannelStatesEnum> STATE = AttributeKey.valueOf("state");

    //TODO 设置channel正在对话的channel
    public static AttributeKey<Channel> TALKING_TO = AttributeKey.valueOf("talkTo");

    /**
     * 设置 Channel 的属性值
     * @param channel 要设置属性的 Netty Channel
     * @param attributeKey 属性的键，使用上面定义的 AttributeKey
     * @param data 要存储的数据
     * @param <T> 数据的类型，使用泛型来适应不同类型的数据
     */
    public static <T> void setAttr(Channel channel, AttributeKey<T> attributeKey, T data) {
        // 获取 Channel 的属性对象
        Attribute<T> attr = channel.attr(attributeKey);
        // 设置属性值
        attr.set(data);
    }

    /**
     * 获取 Channel 的属性值
     * @param channel 要获取属性的 Netty Channel
     * @param attributeKey 属性的键，使用上面定义的 AttributeKey
     * @param <T> 数据的类型，使用泛型来适应不同类型的数据
     * @return 存储在 Channel 中的属性值
     */
    public static <T> T getAttr(Channel channel, AttributeKey<T> attributeKey) {
        if(channel==null){
            return null;
        }
        // 获取并返回属性值
        return channel.attr(attributeKey).get();
    }

    public static <T> void changeState(Channel channel, AttributeKey<T> attributeKey, T from,T to) throws Exception {
        if(getAttr(channel,attributeKey).equals(from)){
            setAttr(channel,attributeKey,to);
        }else {
            throw new Exception("通道类型转换错误");
        }
    }
}
