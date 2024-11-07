package org.lu.zhaodazi.match.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum SexEnum {
    MALE(1, "男性"),
    FEMALE(2, "女性"),
    OTHER(3, "不限");

    private final Integer code;
    private final String description;

    private static Map<String, SexEnum> cache;

    static {
        cache = Arrays.stream(SexEnum.values()).collect(Collectors.toMap(SexEnum::getDescription, Function.identity()));
    }

    public static SexEnum of(String description) {
        return cache.get(description);
    }
}