package com.ycusoft.entity.po;

import com.ycusoft.entity.BaseEntity;

/**
 * 菜单表实体类
 * 对应数据库表: menu
 * @author hq
 * @since 2026-05-25
 */
public class Menu extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    private Long id;

    /**
     * 菜单名称
     */
    private String menuName;

    /**
     * 父菜单id
     */
    private Long parentId;

    /**
     * 排序号
     */
    private Integer orderNum;

    /**
     * 路由地址
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 路由参数
     */
    private String query;

    /**
     * 路由名称
     */
    private String routeName;

    /**
     * 图标
     */
    private String icon;

    /**
     * 备注
     */
    private String remark;

    /**
     * 无参构造方法
     */
    public Menu() {
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
     * 获取菜单名称
     * @return 菜单名称
     */
    public String getMenuName() {
        return menuName;
    }

    /**
     * 设置菜单名称
     * @param menuName 菜单名称
     */
    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    /**
     * 获取父菜单id
     * @return 父菜单id
     */
    public Long getParentId() {
        return parentId;
    }

    /**
     * 设置父菜单id
     * @param parentId 父菜单id
     */
    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    /**
     * 获取排序号
     * @return 排序号
     */
    public Integer getOrderNum() {
        return orderNum;
    }

    /**
     * 设置排序号
     * @param orderNum 排序号
     */
    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    /**
     * 获取路由地址
     * @return 路由地址
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置路由地址
     * @param path 路由地址
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取组件路径
     * @return 组件路径
     */
    public String getComponent() {
        return component;
    }

    /**
     * 设置组件路径
     * @param component 组件路径
     */
    public void setComponent(String component) {
        this.component = component;
    }

    /**
     * 获取路由参数
     * @return 路由参数
     */
    public String getQuery() {
        return query;
    }

    /**
     * 设置路由参数
     * @param query 路由参数
     */
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * 获取路由名称
     * @return 路由名称
     */
    public String getRouteName() {
        return routeName;
    }

    /**
     * 设置路由名称
     * @param routeName 路由名称
     */
    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    /**
     * 获取图标
     * @return 图标
     */
    public String getIcon() {
        return icon;
    }

    /**
     * 设置图标
     * @param icon 图标
     */
    public void setIcon(String icon) {
        this.icon = icon;
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

    public boolean getSort() {
        return false;
    }
}