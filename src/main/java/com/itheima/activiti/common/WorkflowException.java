package com.itheima.activiti.common;

/**
 * 运行时异常
 */
public class WorkflowException extends RuntimeException {

    public WorkflowException(String message) {
        super(message);
    }
}
