package com.itheima.activiti.auth;

import com.itheima.activiti.auth.annotation.IgnoreToken;
import com.itheima.activiti.dto.system.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * JWT token认证过滤器
 */
@Component
@Slf4j
public class JwtAuthenticationTokenFilter implements HandlerInterceptor {

    @Autowired
    private JwtTokenUtils jwtTokenUtils;
    @Autowired
    private JwtSecurityProperties jwtSecurityProperties;

    /**
     * 拦截器的默认方法，每个请求都需要经过此处
     * @param request
     * @param response
     * @param object
     * @return
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) {
        log.debug(request.getRequestURI());
        String token = request.getHeader(jwtSecurityProperties.getHeader());
        // 如果不是映射到方法直接通过
        if (!(object instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod handlerMethod = (HandlerMethod) object;
        Method method = handlerMethod.getMethod();
        //检查是否有忽略token注解，有则跳过认证
        if (method.isAnnotationPresent(IgnoreToken.class)) {
            IgnoreToken ignoreToken = method.getAnnotation(IgnoreToken.class);
            if (ignoreToken.required()) {
                return true;
            }
        }
        // 验证 token
        boolean flag = jwtTokenUtils.validateToken(token);

        if (flag) {
            UserDTO userDTO = jwtTokenUtils.getAuthentication(token);
            JwtContextUtils.setUser(userDTO);
            log.debug("token 验证通过 userDTO:{}", userDTO);
            return true;
        } else {
            response.setStatus(401);
        }
        log.info("Token:{} 验证失败", token);
        return false;
    }
}
