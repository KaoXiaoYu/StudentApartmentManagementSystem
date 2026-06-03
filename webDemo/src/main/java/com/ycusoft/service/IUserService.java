package com.ycusoft.service;

import com.ycusoft.entity.po.User;

import java.util.List;

/**
 * 用户业务逻辑接口
 * 定义用户相关的业务操作方法
 *
 * @author hq
 * @since 2026-05-25
 */
public interface IUserService {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，登录失败返回null
     */
    User login(String username, String password);

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @param realName 真实姓名
     * @return 是否注册成功
     */
    boolean register(String username, String password, String realName);

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    User getUserById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    User getUserByUsername(String username);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<User> getAllUsers();

    /**
     * 创建用户
     *
     * @param user 用户对象
     * @return 是否创建成功
     */
    boolean createUser(User user);

    /**
     * 更新用户信息
     *
     * @param user 用户对象（必须包含ID）
     * @return 是否更新成功
     */
    boolean updateUser(User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Long id);

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return true-可用，false-不可用
     */
    boolean checkUsername(String username);
}
