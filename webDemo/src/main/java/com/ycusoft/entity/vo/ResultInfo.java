package com.ycusoft.entity.vo;

import com.ycusoft.entity.po.User;

import java.io.Serializable;

public class ResultInfo<T> implements Serializable {
    public static final int SUCCESS_CODE = 200;
    public static final int ERROR_CODE = 500;

    private Integer code;
    private String message;
    private T data;

    public ResultInfo() {}
    public ResultInfo(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ResultInfo<T> success(String 登录成功, User user) {
        return new ResultInfo<>(SUCCESS_CODE, "操作成功", null);
    }
    public static <T> ResultInfo<T> success(T data) {
        return new ResultInfo<>(SUCCESS_CODE, "操作成功", data);
    }
    public static <T> ResultInfo<T> error(String msg) {
        return new ResultInfo<>(ERROR_CODE, msg, null);
    }

    // getter/setter
    public Integer getCode() { return code; }
    public void setCode(Integer code) { this.code = code; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}