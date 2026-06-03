package com.ycusoft.dao.impl;

import com.ycusoft.dao.IRoleDao;
import com.ycusoft.entity.po.Role;
import com.ycusoft.utils.DBUtil;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * 角色数据访问实现类
 * 实现角色相关的数据库操作
 *
 * @author hq
 * @since 2026-05-25
 */
public class RoleDaoImpl implements IRoleDao {

    /**
     * 根据角色ID查询角色
     *
     * @param id 角色ID
     * @return 角色对象，不存在返回null
     */
    @Override
    public Role selectById(Long id) {
        String sql = "SELECT id, role_name as roleName, role_key as roleKey, remark, " +
                     "create_time as createTime, create_by as createBy, " +
                     "update_time as updateTime, update_by as updateBy " +
                     "FROM role WHERE id = ?";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanHandler<>(Role.class), id);
        } catch (SQLException e) {
            throw new RuntimeException("查询角色失败", e);
        }
    }

    /**
     * 根据角色标识查询角色
     *
     * @param roleKey 角色标识
     * @return 角色对象，不存在返回null
     */
    @Override
    public Role selectByRoleKey(String roleKey) {
        String sql = "SELECT id, role_name as roleName, role_key as roleKey, remark, " +
                     "create_time as createTime, create_by as createBy, " +
                     "update_time as updateTime, update_by as updateBy " +
                     "FROM role WHERE role_key = ?";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanHandler<>(Role.class), roleKey);
        } catch (SQLException e) {
            throw new RuntimeException("查询角色失败", e);
        }
    }

    /**
     * 查询所有角色
     *
     * @return 角色列表
     */
    @Override
    public List<Role> selectAll() {
        String sql = "SELECT id, role_name as roleName, role_key as roleKey, remark, " +
                     "create_time as createTime, create_by as createBy, " +
                     "update_time as updateTime, update_by as updateBy " +
                     "FROM role ORDER BY create_time DESC";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanListHandler<>(Role.class));
        } catch (SQLException e) {
            throw new RuntimeException("查询角色列表失败", e);
        }
    }

    /**
     * 根据用户ID查询角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<Role> selectByUserId(Long userId) {
        String sql = "SELECT r.id, r.role_name as roleName, r.role_key as roleKey, r.remark, " +
                     "r.create_time as createTime, r.create_by as createBy, " +
                     "r.update_time as updateTime, r.update_by as updateBy " +
                     "FROM role r JOIN user_role ur ON r.id = ur.role_id " +
                     "WHERE ur.user_id = ?";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanListHandler<>(Role.class), userId);
        } catch (SQLException e) {
            throw new RuntimeException("查询用户角色失败", e);
        }
    }

    /**
     * 插入角色记录
     *
     * @param role 角色对象
     * @return 影响的行数
     */
    @Override
    public int insert(Role role) {
        String sql = "INSERT INTO role (role_name, role_key, remark, " +
                     "create_time, create_by, update_time, update_by) " +
                     "VALUES (?, ?, ?, NOW(), ?, NOW(), ?)";
        try {
            return DBUtil.getQueryRunner().update(sql,
                    role.getRoleName(),
                    role.getRoleKey(),
                    role.getRemark(),
                    role.getCreateBy(),
                    role.getUpdateBy());
        } catch (SQLException e) {
            throw new RuntimeException("插入角色失败", e);
        }
    }

    /**
     * 更新角色记录
     *
     * @param role 角色对象（必须包含ID）
     * @return 影响的行数
     */
    @Override
    public int update(Role role) {
        String sql = "UPDATE role SET role_name = ?, role_key = ?, remark = ?, " +
                     "update_time = NOW(), update_by = ? WHERE id = ?";
        try {
            return DBUtil.getQueryRunner().update(sql,
                    role.getRoleName(),
                    role.getRoleKey(),
                    role.getRemark(),
                    role.getUpdateBy(),
                    role.getId());
        } catch (SQLException e) {
            throw new RuntimeException("更新角色失败", e);
        }
    }

    /**
     * 删除角色记录
     *
     * @param id 角色ID
     * @return 影响的行数
     */
    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM role WHERE id = ?";
        try {
            return DBUtil.getQueryRunner().update(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("删除角色失败", e);
        }
    }

    /**
     * 检查角色标识是否存在
     *
     * @param roleKey 角色标识
     * @return true-存在，false-不存在
     */
    @Override
    public boolean existsByRoleKey(String roleKey) {
        String sql = "SELECT COUNT(*) FROM role WHERE role_key = ?";
        try {
            Long count = DBUtil.getQueryRunner().query(sql, new ScalarHandler<>(), roleKey);
            return count != null && count > 0;
        } catch (SQLException e) {
            throw new RuntimeException("检查角色标识失败", e);
        }
    }
}
