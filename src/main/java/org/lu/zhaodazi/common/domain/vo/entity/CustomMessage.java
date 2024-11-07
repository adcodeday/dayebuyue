package org.lu.zhaodazi.common.domain.vo.entity;

import io.netty.channel.ChannelId;
import lombok.Data;
import lombok.Getter;

@Data
public class CustomMessage<T> {
    private Integer type;
    private T data;
    public static Integer START_MATCH = 1;
    public static Integer CANCEL_MATCH = 2;
    public static Integer MATCH_SUC = 3;
    public static Integer MATCH_FAIL = 4;

    private CustomMessage(Integer type, T data){
        this.type=type;
        this.data=data;
    }
    public static CustomMessage<String> startMatch(String channelId){
        return new CustomMessage<>(START_MATCH, channelId);
    }
    public static CustomMessage<String> cancelMatch(String channelId){
        return new CustomMessage<>(CANCEL_MATCH, channelId);
    }
    public static CustomMessage<String[]> matchSuc(String[] channelIds){
        return new CustomMessage<>(MATCH_SUC, channelIds);
    }
    public static CustomMessage<String> matchFail(String channelId){
        return new CustomMessage<>(MATCH_FAIL, channelId);
    }

}
