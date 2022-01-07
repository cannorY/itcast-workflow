package com.itheima.activiti.auth;

import com.itheima.activiti.dto.system.UserDTO;
import io.jsonwebtoken.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * JwtToken的工具类：
 * - 获取jwt的claim：
 *      iss(issuer)  发行人、
 *      sub (subject)  主题
 *      aud (audience) 接收方 用户
 *      exp (expiration time) 到期时间
 *      nbf (not before)  在此之前不可用
 *      iat (issued at)  jwt的签发时间
 * - 创建token
 * - 验证token
 */
@Slf4j
@Component
public class JwtTokenUtils {
    private static final String AUTHORITIES_KEY = "auth";

    @Autowired
    private JwtSecurityProperties jwtSecurityProperties;

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            token = token.replace(jwtSecurityProperties.getTokenStartWith(), "");
            claims = Jwts.parser()
                    .setSigningKey(jwtSecurityProperties.getBase64Secret())
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public String createToken(Map<String, Object> claims) {
        log.info("createToken : {}", claims);
        return jwtSecurityProperties.getTokenStartWith() + Jwts.builder()
                .claim(AUTHORITIES_KEY, claims)
                .setId(UUID.randomUUID().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtSecurityProperties.getTokenValidityInSeconds()))
                .compressWith(CompressionCodecs.DEFLATE)
                .signWith(SignatureAlgorithm.HS512, jwtSecurityProperties.getBase64Secret())
                .compact();
    }

    @SneakyThrows
    public UserDTO getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);

        HashMap map = (HashMap) claims.get(AUTHORITIES_KEY);
        UserDTO userDTO = new UserDTO();
        BeanUtils.populate(userDTO, (Map<String, ? extends Object>) map.get("user"));
        return userDTO;
    }

    public boolean validateToken(String authToken) {
        try {
            authToken = authToken.replace(jwtSecurityProperties.getTokenStartWith(), "");
            Jwts.parser().setSigningKey(jwtSecurityProperties.getBase64Secret()).parseClaimsJws(authToken);
            return true;
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.", e);
        } catch (Exception e) {
            log.info("未知 {}", e.getMessage(), e);
        }
        return false;
    }
}
