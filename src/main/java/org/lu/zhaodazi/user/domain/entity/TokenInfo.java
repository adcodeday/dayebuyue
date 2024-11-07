package org.lu.zhaodazi.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class TokenInfo implements Serializable {
    private Long uid;
    private String uuid;

}
