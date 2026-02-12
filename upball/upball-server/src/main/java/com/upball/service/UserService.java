package com.upball.service;

import com.upball.dto.WxLoginDTO;
import com.upball.entity.User;
import com.upball.vo.LoginVO;

public interface UserService {
    
    /**
     * 微信小程序登录
     */
    LoginVO wxLogin(WxLoginDTO dto);
    
    /**
     * 根据ID获取用户
     */
    User getById(Long id);
    
    /**
     * 更新用户信息
     */
    void updateUser(User user);
}
