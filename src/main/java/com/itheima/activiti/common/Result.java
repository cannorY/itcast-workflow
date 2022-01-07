package com.itheima.activiti.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 返回响应数据
 */
@Data
@ApiModel(description = "返回响应数据")
public class Result<T> {

    @ApiModelProperty(value = "错误信息")
    private String msg;
    @ApiModelProperty(value = "状态码")
    private Integer code;
    @ApiModelProperty(value = "数据")
    private T data;

    public static <T> Result<T> success() {
        Result<T> result = new Result<T>();
        result.data = null;
        result.code = 200;
        result.msg = "";
        return result;
    }

    public static <T> Result<T> success(T object) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 200;
        result.msg = "";
        return result;
    }

    public static <T> Result<T> success(T object, String msg) {
        Result<T> result = new Result<T>();
        result.data = object;
        result.code = 200;
        result.msg = msg;
        return result;
    }

    public static <T> Result<T> error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 500;
        return result;
    }

    public static <T> Result<T> error(T object, String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 500;
        result.data = result.data;
        return result;
    }
}
