package com.ycusoft.service.impl;

import com.ycusoft.dao.IUserDao;
import com.ycusoft.dao.impl.UserDaoImpl;
import com.ycusoft.entity.po.User;
import com.ycusoft.service.IUserService;
import com.ycusoft.utils.PasswordUtil;

import java.util.List;

/**
 * 用户业务逻辑实现类
 * 实现用户相关的业务操作
 *
 * @author hq
 * @since 2026-05-25
 */
public class UserServiceImpl implements IUserService {

    /**
     * 用户数据访问对象
     */
    private final IUserDao userDao;

    /**
     * 用户状态：启用
     */
    private static final String STATUS_ENABLED = "1";

    /**
     * 用户状态：禁用
     */
    private static final String STATUS_DISABLED = "0";

    /**
     * 构造函数
     */
    public UserServiceImpl() {
        this.userDao = new UserDaoImpl();
    }

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，登录失败返回null
     */
    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        User user = userDao.selectByUsername(username);
        
        if (user == null) {
            return null;
        }

        // 检查用户状态
        if (!STATUS_ENABLED.equals(user.getStatus())) {
            return null;
        }

        // 验证密码（密码已加密存储，需要加密后比对）
        String encryptedPassword = PasswordUtil.encrypt(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            return null;
        }

        return user;
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @param realName 真实姓名
     * @return 是否注册成功
     */
    @Override
    public boolean register(String username, String password, String realName) {
        // 检查用户名是否已存在
        if (userDao.existsByUsername(username)) {
            return false;
        }

        // 创建用户对象
        User user = new User();
        user.setUsername(username);
        // 密码加密存储
        user.setPassword(PasswordUtil.encrypt(password));
        user.setRealName(realName);
        user.setStatus(STATUS_ENABLED);
        user.setCreateBy(username);
        user.setUpdateBy(username);

        // 插入用户
        int result = userDao.insert(user);
        return result > 0;
    }

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象，不存在返回null
     */
    @Override
    public User getUserById(Long id) {
        if (id == null || id <= 0) {
            return null;
        }
        return userDao.selectById(id);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象，不存在返回null
     */
    @Override
    public User getUserByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return userDao.selectByUsername(username);
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Override
    public List<User> getAllUsers() {
        return userDao.selectAll();
    }

    /**
     * 创建用户
     *
     * @param user 用户对象
     * @return 是否创建成功
     */
    @Override
    public boolean createUser(User user) {
        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            return false;
        }

        // 检查用户名是否已存在
        if (userDao.existsByUsername(user.getUsername())) {
            return false;
        }

        // 设置默认状态
        if (user.getStatus() == null || user.getStatus().isEmpty()) {
            user.setStatus(STATUS_ENABLED);
        }

        // 密码加密存储
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        }

        int result = userDao.insert(user);
        return result > 0;
    }

    /**
     * 更新用户信息
     *
     * @param user 用户对象（必须包含ID）
     * @return 是否更新成功
     */
    @Override
    public boolean updateUser(User user) {
        if (user == null || user.getId() == null || user.getId() <= 0) {
            return false;
        }

        // 检查用户是否存在
        User existingUser = userDao.selectById(user.getId());
        if (existingUser == null) {
            return false;
        }

        // 如果密码有更新，需要加密
        if (user.getPassword() != null && !user.getPassword().isEmpty() 
                && !user.getPassword().equals(existingUser.getPassword())) {
            user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        }

        int result = userDao.update(user);
        return result > 0;
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteUser(Long id) {
        if (id == null || id <= 0) {
            return false;
        }

        // 检查用户是否存在
        User user = userDao.selectById(id);
        if (user == null) {
            return false;
        }

        int result = userDao.deleteById(id);
        return result > 0;
    }

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return true-可用，false-不可用
     */
    @Override
    public boolean checkUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        return !userDao.existsByUsername(username);
    }
}
