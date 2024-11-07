package org.lu.zhaodazi.websocket.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
@AllArgsConstructor
@Getter
public enum WSRespTypeEnum {
    MATCH_SUC(1, "匹配成功"),
    MESSAGE(2, "消息"),
    LEAVE(3,"对方离开"),
    STATE_ERROR(4,"状态错误"),
    AUTH_FAIL(5,"验证失败"),
    ;

    private final Integer type;
    private final String desc;

    private static Map<Integer, WSRespTypeEnum> cache;

    static {
        cache = Arrays.stream(WSRespTypeEnum.values()).collect(Collectors.toMap(WSRespTypeEnum::getType, Function.identity()));
    }

    public static WSRespTypeEnum of(Integer type) {
        return cache.get(type);
    }
}
