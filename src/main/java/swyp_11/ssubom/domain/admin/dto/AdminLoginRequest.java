package swyp_11.ssubom.domain.admin.dto;

import lombok.Getter;

@Getter
public class AdminLoginRequest {
private String email;
private String password;
private String totpCode;
}
