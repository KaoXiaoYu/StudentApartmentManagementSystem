package com.ycusoft.dao;

import com.ycusoft.entity.po.Role;

import java.util.List;

/**
 * 角色数据访问接口
 * 定义角色相关的数据库操作方法
 *
 * @author hq
 * @since 2026-05-25
 */
public interface IRoleDao {

    /**
     * 根据角色ID查询角色
     *
     * @param id 角色ID
     * @return 角色对象，不存在返回null
     */
    Role selectById(Long id);

    /**
     * 根据角色标识查询角色
     *
     * @param roleKey 角色标识
     * @return 角色对象，不存在返回null
     */
    Role selectByRoleKey(String roleKey);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<Role> selectAll();

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> selectByUserId(Long userId);

    /**
     * 插入角色记录
     *
     * @param role 角色对象
     * @return 影响的行数
     */
    int insert(Role role);

    /**
     * 更新角色记录
     *
     * @param role 角色对象（必须包含ID）
     * @return 影响的行数
     */
    int update(Role role);

    /**
     * 删除角色记录
     *
     * @param id 角色ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 检查角色标识是否存在
     *
     * @param roleKey 角色标识
     * @return true-存在，false-不存在
     */
    boolean existsByRoleKey(String roleKey);
}
