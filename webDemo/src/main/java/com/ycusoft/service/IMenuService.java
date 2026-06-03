package com.ycusoft.service;

import com.ycusoft.entity.po.Menu;

import java.util.List;

/**
 * 菜单业务逻辑接口
 * 定义菜单相关的业务操作方法
 *
 * @author hq
 * @since 2026-05-25
 */
public interface IMenuService {

    /**
     * 根据菜单ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单对象，不存在返回null
     */
    Menu getMenuById(Long id);

    /**
     * 根据菜单路径查询菜单
     *
     * @param path 菜单路径
     * @return 菜单对象，不存在返回null
     */
    Menu getMenuByPath(String path);

    /**
     * 查询所有菜单
     *
     * @return 菜单列表
     */
    List<Menu> getAllMenus();

    /**
     * 根据父菜单ID查询子菜单
     *
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    List<Menu> getMenusByParentId(Long parentId);

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<Menu> getMenusByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单列表
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> getMenusByUserId(Long userId);

    /**
     * 创建菜单
     *
     * @param menu 菜单对象
     * @return 是否创建成功
     */
    boolean createMenu(Menu menu);

    /**
     * 更新菜单信息
     *
     * @param menu 菜单对象（必须包含ID）
     * @return 是否更新成功
     */
    boolean updateMenu(Menu menu);

    /**
     * 删除菜单
     *
     * @param id 菜单ID
     * @return 是否删除成功
     */
    boolean deleteMenu(Long id);

    /**
     * 检查菜单路径是否可用
     *
     * @param path 菜单路径
     * @return true-可用，false-不可用
     */
    boolean checkPath(String path);
}
