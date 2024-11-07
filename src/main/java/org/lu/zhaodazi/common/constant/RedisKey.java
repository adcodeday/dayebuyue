package org.lu.zhaodazi.common.constant;

public class RedisKey {
    //TODO rediskey+++
    private static final String BASE_KEY = "zhaodazi:";
    public static String getKey(String key, Object... objects) {
        return BASE_KEY + String.format(key, objects);
    }

}