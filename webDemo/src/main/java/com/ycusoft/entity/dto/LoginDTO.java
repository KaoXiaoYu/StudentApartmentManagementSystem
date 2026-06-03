package com.ycusoft.entity.dto;

import java.io.Serializable;

/**
 * 登录请求DTO
 * 用于接收前端登录请求参数
 * @author h
 * @since 2026-05-25
 */
public class LoginDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 无参构造方法
     */
    public LoginDTO() {
    }

    /**
     * 全参构造方法
     * @param username 用户名
     * @param password 密码
     */
    public LoginDTO(String username, String password) {
        this.username = username;
        this.password = password;
    }

    /**
     * 获取用户名
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }
}