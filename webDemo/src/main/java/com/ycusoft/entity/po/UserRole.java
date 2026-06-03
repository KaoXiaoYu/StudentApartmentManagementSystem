package com.ycusoft.entity.po;

import java.io.Serializable;

/**
 * 用户角色关联表实体类
 * 对应数据库表: user_role
 * @author hq
 * @since 2026-05-25
 */
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 无参构造方法
     */
    public UserRole() {
    }

    /**
     * 全参构造方法
     * @param userId 用户id
     * @param roleId 角色id
     */
    public UserRole(Long userId, Long roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    /**
     * 获取用户id
     * @return 用户id
     */
    public Long getUserId() {
        return userId;
    }

    /**
     * 设置用户id
     * @param userId 用户id
     */
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    /**
     * 获取角色id
     * @return 角色id
     */
    public Long getRoleId() {
        return roleId;
    }

    /**
     * 设置角色id
     * @param roleId 角色id
     */
    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}