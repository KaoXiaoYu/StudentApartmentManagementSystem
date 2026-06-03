package com.ycusoft.dao.impl;

import com.ycusoft.dao.IUserDao;
import com.ycusoft.entity.po.User;
import com.ycusoft.utils.DBUtil;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

/**
 * 用户数据访问实现类
 * 实现用户相关的数据库操作
 *
 * @author hq
 * @since 2026-05-25
 */
public class UserDaoImpl implements IUserDao {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    @Override
    public User selectByUsername(String username) {
        String sql = "SELECT id, username, password, real_name as realName, status, " +
                     "create_time as createTime, create_by as createBy, " +
                     "update_time as updateTime, update_by as updateBy " +
                     "FROM user WHERE username = ?";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanHandler<>(User.class), username);
        } catch (SQLException e) {
            throw new RuntimeException("查询用户失败", e);
        }
    }

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    @Override
    public User selectById(Long id) {
        String sql = "SELECT id, username, password, real_name as realName, status, " +
                     "create_time as createTime, create_by as createBy, " +
                     "update_time as updateTime, update_by as updateBy " +
                     "FROM user WHERE id = ?";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanHandler<>(User.class), id);
        } catch (SQLException e) {
            throw new RuntimeException("查询用户失败", e);
        }
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Override
    public List<User> selectAll() {
        String sql = "SELECT id, username, password, real_name as realName, status, " +
                     "create_time as createTime, create_by as createBy, " +
                     "update_time as updateTime, update_by as updateBy " +
                     "FROM user ORDER BY create_time DESC";
        try {
            return DBUtil.getQueryRunner().query(sql, new BeanListHandler<>(User.class));
        } catch (SQLException e) {
            throw new RuntimeException("查询用户列表失败", e);
        }
    }

    /**
     * 插入用户记录
     *
     * @param user 用户对象
     * @return 影响的行数
     */
    @Override
    public int insert(User user) {
        String sql = "INSERT INTO user (username, password, real_name, status, " +
                     "create_time, create_by, update_time, update_by) " +
                     "VALUES (?, ?, ?, ?, NOW(), ?, NOW(), ?)";
        try {
            return DBUtil.getQueryRunner().update(sql,
                    user.getUsername(),
                    user.getPassword(),
                    user.getRealName(),
                    user.getStatus(),
                    user.getCreateBy(),
                    user.getUpdateBy());
        } catch (SQLException e) {
            throw new RuntimeException("插入用户失败", e);
        }
    }

    /**
     * 更新用户记录
     *
     * @param user 用户对象（必须包含ID）
     * @return 影响的行数
     */
    @Override
    public int update(User user) {
        String sql = "UPDATE user SET username = ?, password = ?, real_name = ?, " +
                     "status = ?, update_time = NOW(), update_by = ? WHERE id = ?";
        try {
            return DBUtil.getQueryRunner().update(sql,
                    user.getUsername(),
                    user.getPassword(),
                    user.getRealName(),
                    user.getStatus(),
                    user.getUpdateBy(),
                    user.getId());
        } catch (SQLException e) {
            throw new RuntimeException("更新用户失败", e);
        }
    }

    /**
     * 删除用户记录
     *
     * @param id 用户ID
     * @return 影响的行数
     */
    @Override
    public int deleteById(Long id) {
        String sql = "DELETE FROM user WHERE id = ?";
        try {
            return DBUtil.getQueryRunner().update(sql, id);
        } catch (SQLException e) {
            throw new RuntimeException("删除用户失败", e);
        }
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        try {
            Long count = DBUtil.getQueryRunner().query(sql, new ScalarHandler<>(), username);
            return count != null && count > 0;
        } catch (SQLException e) {
            throw new RuntimeException("检查用户名失败", e);
        }
    }
}
