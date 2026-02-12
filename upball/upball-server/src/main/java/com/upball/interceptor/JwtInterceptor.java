package com.upball.interceptor;

import com.upball.utils.JwtUtil;
import com.upball.vo.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class JwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        
        if (token == null || !token.startsWith("Bearer ")) {
            writeError(response, 401, "请先登录");
            return false;
        }

        token = token.substring(7);
        
        try {
            Long userId = jwtUtil.validateToken(token);
            request.setAttribute("userId", userId);
            return true;
        } catch (Exception e) {
            log.warn("Token验证失败: {}", e.getMessage());
            writeError(response, 401, "登录已过期，请重新登录");
            return false;
        }
    }

    private void writeError(HttpServletResponse response, int code, String message) throws Exception {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(200);
        Result<Void> result = Result.error(code, message);
        response.getWriter().write(new ObjectMapper().writeValueAsString(result));
    }
}
