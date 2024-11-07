package org.lu.zhaodazi.websocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@AllArgsConstructor
@Getter
public enum ChannelStatesEnum {
    UNAUTHENTICATED(0),
    AUTHENTICATED(1),
    MATCHING(2),
    CHATTING(3),
    ;
    private final Integer type;
    private static Map<Integer, ChannelStatesEnum> cache;

    static {
        cache = Arrays.stream(ChannelStatesEnum.values()).collect(Collectors.toMap(ChannelStatesEnum::getType, Function.identity()));
    }

    public static ChannelStatesEnum of(Integer type) {
        return cache.get(type);
    }
}
