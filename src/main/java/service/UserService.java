package service;

import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Record;
import model.User;

import java.util.List;

/**
 * 用户业务层服务类
 * 封装用户相关业务逻辑：登录校验、注册新增、信息查询、修改密码、用户管理等
 * 配合 JFinal 框架使用，操作 User 模型完成数据库交互
 */
public class UserService {

    // 后续可在这里编写用户相关业务方法示例模板，预留注释说明如下：

    /**
     * 根据用户名查询用户对象
     * @param username 用户名
     * @return 匹配用户 / null
     */
    // public User findUserByUsername(String username) {
    //     return User.dao.findFirst("select * from user where username = ?", username);
    // }

    /**
     * 用户登录校验
     * @param username 账号
     * @param password 明文密码
     * @return 登录成功返回用户实例，失败返回null
     */
    // public User login(String username, String password) {
    //     User user = findUserByUsername(username);
    //     if (user == null) {
    //         return null;
    //     }
    //     // 使用JFinal自带HashKit校验密码，也可替换为你之前写的PasswordService
    //     if (HashKit.checkPassword(password, user.getStr("password"))) {
    //         return user;
    //     }
    //     return null;
    // }

    /**
     * 查询全部用户列表
     * @return 用户集合
     */
    // public List<User> findAllUser() {
    //     return User.dao.find("select * from user order by id desc");
    // }

    /**
     * 新增注册用户
     * @param username 用户名
     * @param pwd 明文密码
     * @return 保存成功返回true
     */
    // public boolean register(String username, String pwd) {
    //     // 密码加密
    //     String hashPwd = HashKit.hashPassword(pwd);
    //     User user = new User();
    //     user.set("username", username);
    //     user.set("password", hashPwd);
    //     return user.save();
    // }

}