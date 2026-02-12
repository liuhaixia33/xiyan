package com.upball.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WxLoginDTO {
    
    @NotBlank(message = "微信授权码不能为空")
    private String code;
    
    private String nickname;
    private String avatar;
}
