package com.upball.service.impl;

import com.upball.dto.WxLoginDTO;
import com.upball.entity.User;
import com.upball.mapper.UserMapper;
import com.upball.service.UserService;
import com.upball.utils.JwtUtil;
import com.upball.utils.SnowflakeIdUtil;
import com.upball.vo.LoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private SnowflakeIdUtil idUtil;
    
    @Value("${upball.wx.mp.app-id:}")
    private String wxAppId;
    
    @Value("${upball.wx.mp.secret:}")
    private String wxSecret;

    @Autowired
    private HttpUtil httpUtil;

    @Override
    public LoginVO wxLogin(WxLoginDTO dto) {
        // 调用微信接口获取openid
        String url = String.format(
            "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
            wxAppId, wxSecret, dto.getCode()
        );
        
        Map<String, Object> wxResult = httpUtil.getForMap(url);
        
        String openid = (String) wxResult.get("openid");
        String sessionKey = (String) wxResult.get("session_key");
        
        if (openid == null || openid.isEmpty()) {
            log.error("微信登录失败: {}", wxResult);
            throw new RuntimeException("微信登录失败: " + wxResult.get("errmsg"));
        }
        
        User user = userMapper.selectByOpenid(openid);
        
        if (user == null) {
            user = new User();
            user.setId(idUtil.nextId());
            user.setOpenid(openid);
            user.setNickname(dto.getNickname());
            user.setAvatar(dto.getAvatar());
            user.setStatus(1);
            userMapper.insert(user);
            log.info("新用户注册: userId={}", user.getId());
        }
        
        String token = jwtUtil.generateToken(user.getId());
        
        LoginVO vo = new LoginVO();
        vo.setToken(token);
        vo.setExpiresIn(86400L);
        vo.setUserInfo(user);
        
        return vo;
    }

    @Override
    public User getById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public void updateUser(User user) {
        userMapper.updateById(user);
    }
}
