package com.ycusoft.service.impl;

import com.ycusoft.dao.IRoleDao;
import com.ycusoft.dao.impl.RoleDaoImpl;
import com.ycusoft.entity.po.Role;
import com.ycusoft.service.IRoleService;

import java.util.List;

/**
 * 角色业务逻辑实现类
 * 实现角色相关的业务操作
 *
 * @author hq
 * @since 2026-05-25
 */
public class RoleServiceImpl implements IRoleService {

    /**
     * 角色数据访问对象
     */
    private final IRoleDao roleDao;

    /**
     * 构造函数
     */
    public RoleServiceImpl() {
        this.roleDao = new RoleDaoImpl();
    }

    /**
     * 根据角色ID查询角色
     *
     * @param id 角色ID
     * @return 角色对象，不存在返回null
     */
    @Override
    public Role getRoleById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return roleDao.selectById(id);
    }

    /**
     * 根据角色标识查询角色
     *
     * @param roleKey 角色标识
     * @return 角色对象，不存在返回null
     */
    @Override
    public Role getRoleByRoleKey(String roleKey) {
        if (roleKey == null || roleKey.trim().isEmpty()) {
            return null;
        }
        return roleDao.selectByRoleKey(roleKey);
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<Role> getAllRoles() {
        return roleDao.selectAll();
    }

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<Role> getRolesByUserId(Long userId) {
        if (userId == null || userId <= 0) {
            return null;
        }
        return roleDao.selectByUserId(userId);
    }

    /**
     * 创建角色
     *
     * @param role 角色对象
     * @return 是否创建成功
     */
    @Override
    public boolean createRole(Role role) {
        if (role == null || role.getRoleName() == null || role.getRoleName().trim().isEmpty()) {
            return false;
        }

        // 检查角色标识是否已存在
        if (role.getRoleKey() != null && !role.getRoleKey().isEmpty()) {
            if (roleDao.existsByRoleKey(role.getRoleKey())) {
                return false;
            }
        }

        int result = roleDao.insert(role);
        return result > 0;
    }

    /**
     * 更新角色信息
     *
     * @param role 角色对象（必须包含ID）
     * @return 是否更新成功
     */
    @Override
    public boolean updateRole(Role role) {
        if (role == null || role.getId() == null || role.getId() <= 0) {
            return false;
        }

        // 检查角色是否存在
        Role existingRole = roleDao.selectById(role.getId());
        if (existingRole == null) {
            return false;
        }

        int result = roleDao.update(role);
        return result > 0;
    }

    /**
     * 删除角色
     *
     * @param id 角色ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteRole(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        // 检查角色是否存在
        Role role = roleDao.selectById(id);
        if (role == null) {
            return false;
        }

        int result = roleDao.deleteById(id);
        return result > 0;
    }

    /**
     * 检查角色标识是否可用
     *
     * @param roleKey 角色标识
     * @return true-可用，false-不可用
     */
    @Override
    public boolean checkRoleKey(String roleKey) {
        if (roleKey == null || roleKey.trim().isEmpty()) {
            return false;
        }
        return !roleDao.existsByRoleKey(roleKey);
    }
}
