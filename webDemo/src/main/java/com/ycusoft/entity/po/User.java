package com.ycusoft.entity.po;

import com.ycusoft.entity.BaseEntity;

import java.util.List;

/**
 * 用户表实体类
 * 对应数据库表: user
 *
 * @author hq
 * @since 2026-05-25
 */
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码（加密）
     */
    private String password;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 状态：1启用；0禁用
     */
    private String status;

    /**
     * 角色列表
     */
    private List<Role> roles;

    /**
     * 角色key列表
     */
    private List<String> roleKeys;

    /**
     * 权限集合
     */
    private List<String> perms;

    /**
     * 无参构造方法
     */
    public User() {
    }

    /**
     * 获取主键id
     *
     * @return 主键id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键id
     *
     * @param id 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取姓名
     *
     * @return 姓名
     */
    public String getRealName() {
        return realName;
    }

    /**
     * 设置姓名
     *
     * @param realName 姓名
     */
    public void setRealName(String realName) {
        this.realName = realName;
    }

    /**
     * 获取状态
     *
     * @return 状态：1启用；0禁用
     */
    public String getStatus() {
        return status;
    }

    /**
     * 设置状态
     *
     * @param status 状态：1启用；0禁用
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取角色列表
     *
     * @return 角色列表
     */
    public List<Role> getRoles() {
        return roles;
    }

    /**
     * 设置角色列表
     *
     * @param roles 角色列表
     */
    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<String> getRoleKeys() {
        return roleKeys;
    }

    public void setRoleKeys(List<String> roleKeys) {
        this.roleKeys = roleKeys;
    }

    public List<String> getPerms() {
        return perms;
    }

    public void setPerms(List<String> perms) {
        this.perms = perms;
    }
}