package com.itheima.activiti.config;


import com.itheima.activiti.common.WorkflowException;
import com.itheima.activiti.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理
 * 给前端统一返回错误信息
 */
@Configuration
@ControllerAdvice(annotations = {RestController.class, Controller.class})
@ResponseBody
@Slf4j
public class ExceptionConfiguration {

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<String> duplicateKeyException(DuplicateKeyException ex, HttpServletRequest request) {
        log.warn("DuplicateKeyException", ex);

        Result<String> result = null;
        try {
            String[] exMessage = ex.getMessage().split("###");
            String message = exMessage[1].split("Duplicate entry '")[1].split("' for key")[0];
            result = Result.error(message + " 已存在!");
        } catch (Exception e) {
            result = Result.error(e.getMessage());
        }
        return result;
    }

    @ExceptionHandler(WorkflowException.class)
    public Result<String> workflowException(WorkflowException ex, HttpServletRequest request) {
        log.warn("ActivitiException", ex);
        Result<String> result = Result.error(ex.getMessage());
        return result;
    }

    @ExceptionHandler(org.activiti.engine.ActivitiException.class)
    public Result<String> engineActivitiException(org.activiti.engine.ActivitiException ex, HttpServletRequest request) {
        log.warn("EngineActivitiException", ex);
        //org.activiti.engine.ActivitiException: Cannot start process instance. Process definition leave (id = leave:1:f9065993-e51c-11eb-812d-02420a000017) is suspended
        //org.activiti.engine.ActivitiException: Cannot add a comment to a suspended task
        Result<String> result;
        if (ex.getMessage().contains("is suspended") || ex.getMessage().contains("Cannot add a comment to a suspended task")) {
            result = Result.error("流程已挂起");
        } else {
            result = Result.error(ex.getMessage());
        }
        return result;
    }

}

