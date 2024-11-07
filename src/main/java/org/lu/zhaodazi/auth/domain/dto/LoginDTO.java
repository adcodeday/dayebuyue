package org.lu.zhaodazi.auth.domain.dto;

import lombok.*;
import org.lu.zhaodazi.auth.enums.LoginType;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class LoginDTO {

    private String username;

    private String credential;

    private LoginType loginType = LoginType.NORMAL;
}
