package org.lu.zhaodazi.match.domain.dto;

import lombok.Data;
//前端修改condition
@Data
public class MatchingConditionDTO {
    private Long uid;
    private String sex;
    private String province;
    private String wantSex;
    private String wantProvince;
    private String codeWord;
}
