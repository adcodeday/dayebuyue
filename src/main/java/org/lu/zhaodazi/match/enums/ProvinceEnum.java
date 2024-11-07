package org.lu.zhaodazi.match.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
public enum ProvinceEnum {
    BEIJING(1, "北京市"),
    SHANGHAI(2, "上海市"),
    GUANGDONG(3, "广东省"),
    ZHEJIANG(4, "浙江省"),
    JIANGSU(5, "江苏省"),
    TIANJIN(6, "天津市"),
    HEBEI(7, "河北省"),
    SHANXI(8, "山西省"),
    INNER_MONGOLIA(9, "内蒙古自治区"),
    LIAONING(10, "辽宁省"),
    JILIN(11, "吉林省"),
    HEILONGJIANG(12, "黑龙江省"),
    FUJIAN(13, "福建省"),
    SHANDONG(14, "山东省"),
    HENAN(15, "河南省"),
    HUBEI(16, "湖北省"),
    HUNAN(17, "湖南省"),
    JIANGXI(18, "江西省"),
    ANHUI(19, "安徽省"),
    GUANGXI(20, "广西壮族自治区"),
    HAINAN(21, "海南省"),
    CHONGQING(22, "重庆市"),
    SICHUAN(23, "四川省"),
    GUIZHOU(24, "贵州省"),
    YUNNAN(25, "云南省"),
    XIZANG(26, "西藏自治区"),
    SHAANXI(27, "陕西省"),
    GANSU(28, "甘肃省"),
    QINGHAI(29, "青海省"),
    NINGXIA(30, "宁夏回族自治区"),
    XINJIANG(31, "新疆维吾尔自治区"),
    TAIWAN(32, "台湾省"),
    HONGKONG(33, "香港特别行政区"),
    MACAO(34, "澳门特别行政区"),
    NULL(35, "不限");

    private final Integer code;
    private final String name;

    private static Map<String, ProvinceEnum> cache;

    static {
        cache = Arrays.stream(ProvinceEnum.values()).collect(Collectors.toMap(ProvinceEnum::getName, Function.identity()));
    }

    public static ProvinceEnum of(String name) {
        return cache.get(name);
    }
}