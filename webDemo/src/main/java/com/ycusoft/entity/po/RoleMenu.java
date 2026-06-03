package com.ycusoft.entity.po;

import java.io.Serializable;

/**
 * 角色菜单关联表实体类
 * 对应数据库表: role_menu
 * @author hq
 * @since 2026-05-25
 */
public class RoleMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 角色id
     */
    private Long roleId;

    /**
     * 菜单id
     */
    private Long menuId;

    /**
     * 无参构造方法
     */
    public RoleMenu() {
    }

    /**
     * 全参构造方法
     * @param roleId 角色id
     * @param menuId 菜单id
     */
    public RoleMenu(Long roleId, Long menuId) {
        this.roleId = roleId;
        this.menuId = menuId;
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

    /**
     * 获取菜单id
     * @return 菜单id
     */
    public Long getMenuId() {
        return menuId;
    }

    /**
     * 设置菜单id
     * @param menuId 菜单id
     */
    public void setMenuId(Long menuId) {
        this.menuId = menuId;
    }
}