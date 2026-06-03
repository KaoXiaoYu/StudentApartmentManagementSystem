package com.ycusoft.dao;

import com.ycusoft.entity.po.User;

import java.util.List;

/**
 * 用户数据访问接口
 * 定义用户相关的数据库操作方法
 *
 * @author hq
 * @since 2026-05-25
 */
public interface IUserDao {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    User selectByUsername(String username);

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    User selectById(Long id);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<User> selectAll();

    /**
     * 插入用户记录
     *
     * @param user 用户对象
     * @return 影响的行数
     */
    int insert(User user);

    /**
     * 更新用户记录
     *
     * @param user 用户对象（必须包含ID）
     * @return 影响的行数
     */
    int update(User user);

    /**
     * 删除用户记录
     *
     * @param id 用户ID
     * @return 影响的行数
     */
    int deleteById(Long id);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    boolean existsByUsername(String username);
}



