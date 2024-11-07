package org.lu.zhaodazi.match.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lu.zhaodazi.match.domain.entity.MatchingCondition;
//接收匹配消息
@AllArgsConstructor
@Getter
public class Match {
    private MatchingCondition matchingCondition;
    private String channelId;
}
