package com.ycusoft.entity.po;

import com.ycusoft.entity.BaseEntity;

/**
 * 角色表实体类
 * 对应数据库表: role
 * @author hq
 * @since 2026-05-25
 */
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色标识
     */
    private String roleKey;

    /**
     * 备注
     */
    private String remark;

    /**
     * 无参构造方法
     */
    public Role() {
    }

    /**
     * 获取主键id
     * @return 主键id
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置主键id
     * @param id 主键id
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取角色名称
     * @return 角色名称
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * 设置角色名称
     * @param roleName 角色名称
     */
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    /**
     * 获取角色标识
     * @return 角色标识
     */
    public String getRoleKey() {
        return roleKey;
    }

    /**
     * 设置角色标识
     * @param roleKey 角色标识
     */
    public void setRoleKey(String roleKey) {
        this.roleKey = roleKey;
    }

    /**
     * 获取备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}