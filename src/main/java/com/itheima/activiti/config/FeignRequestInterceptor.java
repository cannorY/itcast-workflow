package com.itheima.activiti.config;

import com.itheima.activiti.auth.JwtContextUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Feign接口统一添加请求头，权限管家需要
 * 用于分辨是权限管家平台级或者是应用级请求
 * - APPLICATIONID：
 * - APPLICATIONTYPE
 */
@Slf4j
@Component
public class FeignRequestInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        if (JwtContextUtils.getUser() != null) {
            requestTemplate.header("APPLICATIONID", JwtContextUtils.getUser().getApplicationId());
            requestTemplate.header("APPLICATIONTYPE", "APPLICATION");
        }
    }
}
