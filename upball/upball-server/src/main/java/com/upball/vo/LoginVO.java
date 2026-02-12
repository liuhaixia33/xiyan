package com.upball.vo;

import com.upball.entity.User;
import lombok.Data;

@Data
public class LoginVO {
    
    private String token;
    private String refreshToken;
    private Long expiresIn;
    private User userInfo;
}
