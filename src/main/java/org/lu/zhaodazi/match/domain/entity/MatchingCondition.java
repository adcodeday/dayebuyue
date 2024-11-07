package org.lu.zhaodazi.match.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lu.zhaodazi.match.enums.ProvinceEnum;
import org.lu.zhaodazi.match.enums.SexEnum;

@AllArgsConstructor
@Getter
public class MatchingCondition {
    private Integer sex;
    private Integer province;
    private Integer wantSex;
    private Integer wantProvince;
    private String codeWord;
}
