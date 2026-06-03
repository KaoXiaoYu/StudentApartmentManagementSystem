package com.ycusoft.service;

import com.ycusoft.entity.po.Role;

import java.util.List;

/**
 * 角色业务逻辑接口
 * 定义角色相关的业务操作方法
 *
 * @author hq
 * @since 2026-05-25
 */
public interface IRoleService {

    /**
     * 根据角色ID查询角色
     *
     * @param id 角色ID
     * @return 角色对象，不存在返回null
     */
    Role getRoleById(Long id);

    /**
     * 根据角色标识查询角色
     *
     * @param roleKey 角色标识
     * @return 角色对象，不存在返回null
     */
    Role getRoleByRoleKey(String roleKey);

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    List<Role> getAllRoles();

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 创建角色
     *
     * @param role 角色对象
     * @return 是否创建成功
     */
    boolean createRole(Role role);

    /**
     * 更新角色信息
     *
     * @param role 角色对象（必须包含ID）
     * @return 是否更新成功
     */
    boolean updateRole(Role role);

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否删除成功
     */
    boolean deleteRole(Long id);

    /**
     * 检查角色标识是否可用
     *
     * @param roleKey 角色标识
     * @return true-可用，false-不可用
     */
    boolean checkRoleKey(String roleKey);
}
