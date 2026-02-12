package com.upball.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${upball.jwt.secret:upball-secret-key}")
    private String secret;

    @Value("${upball.jwt.expire:86400000}")
    private Long expire;

    /**
     * 生成Token
     */
    public String generateToken(Long userId) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expire);

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withIssuedAt(now)
                .withExpiresAt(expiration)
                .sign(Algorithm.HMAC256(secret));
    }

    /**
     * 验证Token并返回用户ID
     */
    public Long validateToken(String token) {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(secret)).build();
        DecodedJWT jwt = verifier.verify(token);
        return Long.valueOf(jwt.getSubject());
    }

    /**
     * 从Token获取用户ID（不验证）
     */
    public Long getUserIdFromToken(String token) {
        DecodedJWT jwt = JWT.decode(token);
        return Long.valueOf(jwt.getSubject());
    }
}
