package com.upball.controller;

import com.upball.dto.WxLoginDTO;
import com.upball.service.UserService;
import com.upball.vo.LoginVO;
import com.upball.vo.Result;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    /**
     * 微信小程序登录
     */
    @PostMapping("/wx-login")
    public Result<LoginVO> wxLogin(@Valid @RequestBody WxLoginDTO dto) {
        LoginVO vo = userService.wxLogin(dto);
        return Result.success(vo);
    }
}
