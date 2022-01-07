package com.itheima.activiti.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.itheima.activiti.auth.JwtContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/**
 * 业务表单中自动注入：
 * 创建人、创建时间
 * 修改人、修改时间的值
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject, "createTime", () -> LocalDateTime.now(), LocalDateTime.class);
        this.strictInsertFill(metaObject, "createUser", () -> JwtContextUtils.getUser().getId(), Long.class);
        this.strictInsertFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class);
        this.strictInsertFill(metaObject, "updateUser", () -> JwtContextUtils.getUser().getId(), Long.class);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.strictUpdateFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class);
        this.strictUpdateFill(metaObject, "updateUser", () -> JwtContextUtils.getUser().getId(), Long.class);
    }
}
