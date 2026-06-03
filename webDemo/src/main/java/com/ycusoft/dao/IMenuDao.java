package com.ycusoft.dao;

import com.ycusoft.entity.po.Menu;

import java.util.List;

/**
 * 菜单数据访问接口
 * 定义菜单相关的数据库操作方法
 *
 * @author hq
 * @since 2026-05-25
 */
public interface IMenuDao {

    /**
     * 根据菜单ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单对象，不存在返回null
     */
    Menu selectById(Long id);

    /**
     * 根据菜单路径查询菜单
     *
     * @param path 菜单路径
     * @return 菜单对象，不存在返回null
     */
    Menu selectByPath(String path);

    /**
     * 查询所有菜单
     *
     * @return 菜单列表
     */
    List<Menu> selectAll();

    /**
     * 根据父菜单ID查询子菜单
     *
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    List<Menu> selectByParentId(Long parentId);

    /**
     * 根据角色ID查询菜单列表
     *
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<Menu> selectByRoleId(Long roleId);

    /**
     * 根据用户ID查询菜单列表（通过角色关联）
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> selectByUserId(Long userId);

    /**
     * 插入菜单记录
     *
     * @param menu 菜单对象
     * @return 影响的行数
     */
    int insert(Menu menu);

    /**
     * 更新菜单记录
     *
     * @param menu 菜单对象（必须包含ID）
     * @return 影响的行数
     */
    int update(Menu menu);

    /**
     * 删除菜单记录
     *
     * @param id 菜单ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 检查菜单路径是否存在
     *
     * @param path 菜单路径
     * @return true-存在，false-不存在
     */
    boolean existsByPath(String path);
}
