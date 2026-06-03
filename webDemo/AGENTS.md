# 通用权限管理系统 - 后端技术方案文档

## 1. 技术栈

| 技术 | 版本 | 说明 |
| :--- | :--- | :--- |
| Servlet | 4.0+ | Java Web 核心技术 |
| MySQL | 8.0+ | 关系型数据库 |
| Druid | 1.2.x | 数据库连接池 |
| DBUtils | 1.7 | Apache 数据库操作工具 |
| Jackson | 2.13+ | JSON 序列化/反序列化 |

---

## 2. 项目结构

```plaintext
src/
├── main/
│   ├── java/
│   │   └── com/example/auth/
│   │       ├── controller/          # Servlet 控制器层
│   │       │   └── UserServlet.java
│   │       ├── service/             # 业务逻辑层
│   │       │   ├── IUserService.java
│   │       │   └── impl/
│   │       │       └── UserServiceImpl.java
│   │       ├── dao/                 # 数据访问层
│   │       │   ├── IUserDao.java
│   │       │   └── impl/
│   │       │       └── UserDaoImpl.java
│   │       ├── entity/              # 实体类
│   │       │   └── User.java
│   │       ├── enums/               # 枚举类
│   │       │   └── ResultCode.java
│   │       ├── constant/            # 常量类
│   │       │   └── Constants.java
│   │       ├── util/                # 工具类
│   │       │   ├── DBUtil.java
│   │       │   └── JSONUtil.java
│   │       └── vo/                  # 值对象
│   │           └── ResultInfo.java
│   └── resources/
│       └── db.properties            # 数据库配置
└── webapp/
    └── WEB-INF/
        └── web.xml                  # Web 配置
```

---

## 3. 常量类

### 3.1 Constants.java

```java
package com.example.auth.constant;

/**
 * 系统常量类
 * 定义系统中所有的常量值，避免硬编码
 *
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
public final class Constants {

    /**
     * 私有构造函数，防止实例化
     */
    private Constants() {
        // 防止被实例化
    }

    // ==================== 数据库相关常量 ====================

    /**
     * 数据库配置文件路径
     */
    public static final String DB_CONFIG_PATH = "db.properties";

    /**
     * 数据库驱动类名
     */
    public static final String DB_DRIVER_KEY = "driverClassName";

    /**
     * 数据库连接URL
     */
    public static final String DB_URL_KEY = "url";

    /**
     * 数据库用户名
     */
    public static final String DB_USERNAME_KEY = "username";

    /**
     * 数据库密码
     */
    public static final String DB_PASSWORD_KEY = "password";

    // ==================== 分页相关常量 ====================

    /**
     * 默认页码
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 默认每页条数
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    /**
     * 最大每页条数
     */
    public static final int MAX_PAGE_SIZE = 100;

    // ==================== 正则表达式常量 ====================

    /**
     * 用户名正则：4-20位字母、数字、下划线
     */
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{4,20}$";

    /**
     * 密码正则：6-32位，至少包含字母和数字
     */
    public static final String PASSWORD_REGEX = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,32}$";

    /**
     * 邮箱正则
     */
    public static final String EMAIL_REGEX = "^[\\w.-]+@[\\w.-]+\\.\\w{2,}$";

    /**
     * 手机号正则
     */
    public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    // ==================== 请求参数常量 ====================

    /**
     * 请求参数：用户名
     */
    public static final String PARAM_USERNAME = "username";

    /**
     * 请求参数：密码
     */
    public static final String PARAM_PASSWORD = "password";

    /**
     * 请求参数：邮箱
     */
    public static final String PARAM_EMAIL = "email";

    /**
     * 请求参数：手机号
     */
    public static final String PARAM_PHONE = "phone";

    /**
     * 请求参数：页码
     */
    public static final String PARAM_PAGE_NUM = "pageNum";

    /**
     * 请求参数：每页条数
     */
    public static final String PARAM_PAGE_SIZE = "pageSize";

    // ==================== 响应消息常量 ====================

    /**
     * 操作成功消息
     */
    public static final String MESSAGE_SUCCESS = "操作成功";

    /**
     * 操作失败消息
     */
    public static final String MESSAGE_FAILURE = "操作失败";

    /**
     * 参数校验失败消息
     */
    public static final String MESSAGE_PARAM_ERROR = "参数校验失败";

    /**
     * 用户不存在消息
     */
    public static final String MESSAGE_USER_NOT_FOUND = "用户不存在";

    /**
     * 密码错误消息
     */
    public static final String MESSAGE_PASSWORD_ERROR = "密码错误";

    /**
     * 用户已存在消息
     */
    public static final String MESSAGE_USER_EXISTS = "用户已存在";

    /**
     * 登录成功消息
     */
    public static final String MESSAGE_LOGIN_SUCCESS = "登录成功";

    /**
     * 登出成功消息
     */
    public static final String MESSAGE_LOGOUT_SUCCESS = "登出成功";

    // ==================== 字符编码常量 ====================

    /**
     * UTF-8 编码
     */
    public static final String CHARSET_UTF8 = "UTF-8";

    /**
     * Content-Type: JSON
     */
    public static final String CONTENT_TYPE_JSON = "application/json;charset=UTF-8";
}
```

---

## 4. 枚举类

### 4.1 ResultCode.java

```java
package com.example.auth.enums;

/**
 * 响应状态码枚举类
 * 定义系统中所有的响应状态码及其含义
 *
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
public enum ResultCode {

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未授权
     */
    UNAUTHORIZED(401, "未授权"),

    /**
     * 拒绝访问
     */
    FORBIDDEN(403, "拒绝访问"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 服务器内部错误
     */
    INTERNAL_ERROR(500, "服务器内部错误"),

    /**
     * 数据库操作异常
     */
    DB_ERROR(501, "数据库操作异常"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(1001, "用户不存在"),

    /**
     * 密码错误
     */
    PASSWORD_ERROR(1002, "密码错误"),

    /**
     * 用户已存在
     */
    USER_EXISTS(1003, "用户已存在"),

    /**
     * 用户名或密码不能为空
     */
    USERNAME_PASSWORD_EMPTY(1004, "用户名或密码不能为空"),

    /**
     * 用户名格式错误
     */
    USERNAME_FORMAT_ERROR(1005, "用户名格式错误，需4-20位字母、数字、下划线"),

    /**
     * 密码格式错误
     */
    PASSWORD_FORMAT_ERROR(1006, "密码格式错误，需6-32位，至少包含字母和数字"),

    /**
     * 邮箱格式错误
     */
    EMAIL_FORMAT_ERROR(1007, "邮箱格式错误"),

    /**
     * 手机号格式错误
     */
    PHONE_FORMAT_ERROR(1008, "手机号格式错误");

    /**
     * 状态码
     */
    private final int code;

    /**
     * 状态消息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 状态消息
     */
    ResultCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取状态消息
     *
     * @return 状态消息
     */
    public String getMessage() {
        return message;
    }
}
```

---

## 5. 实体类

### 5.1 User.java

```java
package com.example.auth.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户实体类
 * 对应数据库表 `t_user`
 *
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
public class User implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID，主键，自增
     */
    private Long id;

    /**
     * 用户名，唯一
     */
    private String username;

    /**
     * 密码（加密存储）
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 无参构造函数
     */
    public User() {
        // 默认构造函数
    }

    /**
     * 全参构造函数
     *
     * @param id         用户ID
     * @param username   用户名
     * @param password   密码
     * @param email      邮箱
     * @param phone      手机号
     * @param status     用户状态
     * @param createTime 创建时间
     * @param updateTime 更新时间
     */
    public User(Long id, String username, String password, String email, String phone,
                Integer status, Date createTime, Date updateTime) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置用户ID
     *
     * @param id 用户ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取用户名
     *
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码
     *
     * @return 密码
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码
     *
     * @param password 密码
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取邮箱
     *
     * @return 邮箱
     */
    public String getEmail() {
        return email;
    }

    /**
     * 设置邮箱
     *
     * @param email 邮箱
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * 获取手机号
     *
     * @return 手机号
     */
    public String getPhone() {
        return phone;
    }

    /**
     * 设置手机号
     *
     * @param phone 手机号
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * 获取用户状态
     *
     * @return 用户状态
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置用户状态
     *
     * @param status 用户状态
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取创建时间
     *
     * @return 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 重写toString方法
     *
     * @return 对象字符串表示
     */
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
```

---

## 6. 值对象

### 6.1 ResultInfo.java

```java
package com.example.auth.vo;

import com.example.auth.enums.ResultCode;

import java.io.Serializable;

/**
 * 统一响应结果封装类
 * 所有接口除文件下载外，统一使用此类进行封装
 *
 * @param <T> 数据类型
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
public class ResultInfo<T> implements Serializable {

    /**
     * 序列化版本号
     */
    private static final long serialVersionUID = 1L;

    /**
     * 状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 异常信息（仅在发生错误时返回）
     */
    private String error;

    /**
     * 无参构造函数
     */
    public ResultInfo() {
        // 默认构造函数
    }

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 响应消息
     */
    public ResultInfo(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 响应消息
     * @param data    响应数据
     */
    public ResultInfo(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    /**
     * 构造函数
     *
     * @param code    状态码
     * @param message 响应消息
     * @param data    响应数据
     * @param error   异常信息
     */
    public ResultInfo(int code, String message, T data, String error) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    /**
     * 创建成功响应
     *
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> ResultInfo<T> success() {
        return new ResultInfo<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage());
    }

    /**
     * 创建成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应对象
     */
    public static <T> ResultInfo<T> success(T data) {
        return new ResultInfo<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }

    /**
     * 创建成功响应（自定义消息）
     *
     * @param message 响应消息
     * @param data    响应数据
     * @param <T>     数据类型
     * @return 成功响应对象
     */
    public static <T> ResultInfo<T> success(String message, T data) {
        return new ResultInfo<>(ResultCode.SUCCESS.getCode(), message, data);
    }

    /**
     * 创建失败响应
     *
     * @param resultCode 错误状态码枚举
     * @param <T>        数据类型
     * @return 失败响应对象
     */
    public static <T> ResultInfo<T> error(ResultCode resultCode) {
        return new ResultInfo<>(resultCode.getCode(), resultCode.getMessage());
    }

    /**
     * 创建失败响应（带异常信息）
     *
     * @param resultCode 错误状态码枚举
     * @param error      异常信息
     * @param <T>        数据类型
     * @return 失败响应对象
     */
    public static <T> ResultInfo<T> error(ResultCode resultCode, String error) {
        return new ResultInfo<>(resultCode.getCode(), resultCode.getMessage(), null, error);
    }

    /**
     * 创建失败响应（自定义状态码和消息）
     *
     * @param code    状态码
     * @param message 响应消息
     * @param <T>     数据类型
     * @return 失败响应对象
     */
    public static <T> ResultInfo<T> error(int code, String message) {
        return new ResultInfo<>(code, message);
    }

    /**
     * 获取状态码
     *
     * @return 状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 设置状态码
     *
     * @param code 状态码
     */
    public void setCode(int code) {
        this.code = code;
    }

    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    public String getMessage() {
        return message;
    }

    /**
     * 设置响应消息
     *
     * @param message 响应消息
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 获取响应数据
     *
     * @return 响应数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置响应数据
     *
     * @param data 响应数据
     */
    public void setData(T data) {
        this.data = data;
    }

    /**
     * 获取异常信息
     *
     * @return 异常信息
     */
    public String getError() {
        return error;
    }

    /**
     * 设置异常信息
     *
     * @param error 异常信息
     */
    public void setError(String error) {
        this.error = error;
    }

    /**
     * 判断是否成功
     *
     * @return true-成功，false-失败
     */
    public boolean isSuccess() {
        return this.code == ResultCode.SUCCESS.getCode();
    }

    /**
     * 重写toString方法
     *
     * @return 对象字符串表示
     */
    @Override
    public String toString() {
        return "ResultInfo{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", error='" + error + '\'' +
                '}';
    }
}
```

---

## 7. 工具类

### 7.1 DBUtil.java

```java
package com.example.auth.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.example.auth.constant.Constants;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * 数据库操作工具类
 * 基于 Druid 连接池和 DBUtils 实现数据库操作
 *
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
public class DBUtil {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(DBUtil.class);

    /**
     * Druid 数据源
     */
    private static volatile DataSource dataSource;

    /**
     * DBUtils QueryRunner
     */
    private static QueryRunner queryRunner;

    /**
     * 静态代码块：初始化数据源
     */
    static {
        initDataSource();
    }

    /**
     * 私有构造函数，防止实例化
     */
    private DBUtil() {
        // 防止被实例化
    }

    /**
     * 初始化数据源
     */
    private static synchronized void initDataSource() {
        if (dataSource == null) {
            try {
                Properties properties = new Properties();
                InputStream inputStream = DBUtil.class.getClassLoader()
                        .getResourceAsStream(Constants.DB_CONFIG_PATH);
                
                if (inputStream == null) {
                    logger.error("数据库配置文件 {} 未找到", Constants.DB_CONFIG_PATH);
                    throw new RuntimeException("数据库配置文件未找到");
                }
                
                properties.load(inputStream);
                dataSource = DruidDataSourceFactory.createDataSource(properties);
                queryRunner = new QueryRunner(dataSource);
                
                logger.info("数据库连接池初始化成功");
            } catch (IOException e) {
                logger.error("加载数据库配置文件失败", e);
                throw new RuntimeException("加载数据库配置文件失败", e);
            } catch (Exception e) {
                logger.error("初始化数据库连接池失败", e);
                throw new RuntimeException("初始化数据库连接池失败", e);
            }
        }
    }

    /**
     * 获取数据库连接
     *
     * @return Connection 对象
     * @throws SQLException SQL异常
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initDataSource();
        }
        return dataSource.getConnection();
    }

    /**
     * 获取数据源
     *
     * @return DataSource 对象
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            initDataSource();
        }
        return dataSource;
    }

    /**
     * 执行查询，返回单个对象
     *
     * @param sql       SQL语句
     * @param beanClass 返回对象类型
     * @param params    SQL参数
     * @param <T>       返回类型泛型
     * @return 查询结果对象
     */
    public static <T> T queryForObject(String sql, Class<T> beanClass, Object... params) {
        try {
            return queryRunner.query(sql, new BeanHandler<>(beanClass), params);
        } catch (SQLException e) {
            logger.error("查询单条记录失败，SQL: {}, 参数: {}", sql, params, e);
            throw new RuntimeException("查询数据失败", e);
        }
    }

    /**
     * 执行查询，返回对象列表
     *
     * @param sql       SQL语句
     * @param beanClass 返回对象类型
     * @param params    SQL参数
     * @param <T>       返回类型泛型
     * @return 查询结果列表
     */
    public static <T> List<T> queryForList(String sql, Class<T> beanClass, Object... params) {
        try {
            return queryRunner.query(sql, new BeanListHandler<>(beanClass), params);
        } catch (SQLException e) {
            logger.error("查询多条记录失败，SQL: {}, 参数: {}", sql, params, e);
            throw new RuntimeException("查询数据失败", e);
        }
    }

    /**
     * 执行查询，返回标量值（如count、sum等）
     *
     * @param sql    SQL语句
     * @param params SQL参数
     * @param <T>    返回类型泛型
     * @return 标量值
     */
    public static <T> T queryForScalar(String sql, Object... params) {
        try {
            return queryRunner.query(sql, new ScalarHandler<>(), params);
        } catch (SQLException e) {
            logger.error("查询标量值失败，SQL: {}, 参数: {}", sql, params, e);
            throw new RuntimeException("查询数据失败", e);
        }
    }

    /**
     * 执行更新操作（INSERT/UPDATE/DELETE）
     *
     * @param sql    SQL语句
     * @param params SQL参数
     * @return 影响的行数
     */
    public static int update(String sql, Object... params) {
        try {
            return queryRunner.update(sql, params);
        } catch (SQLException e) {
            logger.error("执行更新操作失败，SQL: {}, 参数: {}", sql, params, e);
            throw new RuntimeException("更新数据失败", e);
        }
    }

    /**
     * 执行批量更新操作
     *
     * @param sql    SQL语句
     * @param params 参数数组（二维数组）
     * @return 每个更新操作影响的行数数组
     */
    public static int[] batchUpdate(String sql, Object[][] params) {
        try {
            return queryRunner.batch(sql, params);
        } catch (SQLException e) {
            logger.error("执行批量更新操作失败，SQL: {}", sql, e);
            throw new RuntimeException("批量更新数据失败", e);
        }
    }

    /**
     * 开启事务
     *
     * @param connection 数据库连接
     * @throws SQLException SQL异常
     */
    public static void beginTransaction(Connection connection) throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.setAutoCommit(false);
            logger.debug("事务已开启");
        }
    }

    /**
     * 提交事务
     *
     * @param connection 数据库连接
     * @throws SQLException SQL异常
     */
    public static void commitTransaction(Connection connection) throws SQLException {
        if (connection != null && !connection.getAutoCommit()) {
            connection.commit();
            logger.debug("事务已提交");
        }
    }

    /**
     * 回滚事务
     *
     * @param connection 数据库连接
     * @throws SQLException SQL异常
     */
    public static void rollbackTransaction(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                    logger.debug("事务已回滚");
                }
            } catch (SQLException e) {
                logger.error("回滚事务失败", e);
            }
        }
    }

    /**
     * 关闭连接
     *
     * @param connection 数据库连接
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("关闭数据库连接失败", e);
            }
        }
    }

    /**
     * 关闭连接并恢复自动提交状态
     *
     * @param connection     数据库连接
     * @param originalAutoCommit 原始自动提交状态
     */
    public static void closeConnection(Connection connection, boolean originalAutoCommit) {
        if (connection != null) {
            try {
                connection.setAutoCommit(originalAutoCommit);
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                logger.error("关闭数据库连接失败", e);
            }
        }
    }
}
```

### 7.2 JSONUtil.java

```java
package com.example.auth.util;

import com.example.auth.constant.Constants;
import com.example.auth.enums.ResultCode;
import com.example.auth.vo.ResultInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * JSON 工具类
 * 基于 Jackson 实现 JSON 序列化和反序列化
 *
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
public class JSONUtil {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    /**
     * Jackson ObjectMapper 实例（线程安全）
     */
    private static final ObjectMapper objectMapper;

    /**
     * 静态代码块：初始化 ObjectMapper
     */
    static {
        objectMapper = new ObjectMapper();
        // 注册 Java 8 日期时间模块
        objectMapper.registerModule(new JavaTimeModule());
        // 禁用日期时间戳格式
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 允许序列化空对象
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private JSONUtil() {
        // 防止被实例化
    }

    /**
     * 将对象序列化为 JSON 字符串
     *
     * @param obj 待序列化对象
     * @return JSON 字符串
     */
    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            logger.error("JSON 序列化失败，对象: {}", obj, e);
            throw new RuntimeException("JSON 序列化失败", e);
        }
    }

    /**
     * 将 JSON 字符串反序列化为对象
     *
     * @param json      JSON 字符串
     * @param valueType 目标对象类型
     * @param <T>       目标类型泛型
     * @return 反序列化后的对象
     */
    public static <T> T fromJson(String json, Class<T> valueType) {
        try {
            return objectMapper.readValue(json, valueType);
        } catch (Exception e) {
            logger.error("JSON 反序列化失败，JSON: {}", json, e);
            throw new RuntimeException("JSON 反序列化失败", e);
        }
    }

    /**
     * 将对象写入 HTTP 响应
     *
     * @param response HTTP 响应对象
     * @param obj      待序列化对象
     */
    public static void writeJson(HttpServletResponse response, Object obj) {
        response.setContentType(Constants.CONTENT_TYPE_JSON);
        response.setCharacterEncoding(Constants.CHARSET_UTF8);
        
        try (PrintWriter writer = response.getWriter()) {
            String json = toJson(obj);
            writer.write(json);
            writer.flush();
            logger.debug("已向响应写入 JSON: {}", json);
        } catch (IOException e) {
            logger.error("写入 JSON 响应失败", e);
            throw new RuntimeException("写入 JSON 响应失败", e);
        }
    }

    /**
     * 将成功响应写入 HTTP 响应
     *
     * @param response HTTP 响应对象
     * @param data     响应数据
     */
    public static void writeSuccess(HttpServletResponse response, Object data) {
        ResultInfo<Object> result = ResultInfo.success(data);
        writeJson(response, result);
    }

    /**
     * 将成功响应写入 HTTP 响应（自定义消息）
     *
     * @param response HTTP 响应对象
     * @param message  响应消息
     * @param data     响应数据
     */
    public static void writeSuccess(HttpServletResponse response, String message, Object data) {
        ResultInfo<Object> result = ResultInfo.success(message, data);
        writeJson(response, result);
    }

    /**
     * 将失败响应写入 HTTP 响应
     *
     * @param response   HTTP 响应对象
     * @param resultCode 错误状态码枚举
     */
    public static void writeError(HttpServletResponse response, ResultCode resultCode) {
        ResultInfo<Object> result = ResultInfo.error(resultCode);
        writeJson(response, result);
    }

    /**
     * 将失败响应写入 HTTP 响应（带异常信息）
     *
     * @param response   HTTP 响应对象
     * @param resultCode 错误状态码枚举
     * @param error      异常信息
     */
    public static void writeError(HttpServletResponse response, ResultCode resultCode, String error) {
        ResultInfo<Object> result = ResultInfo.error(resultCode, error);
        writeJson(response, result);
    }

    /**
     * 将失败响应写入 HTTP 响应（自定义状态码和消息）
     *
     * @param response HTTP 响应对象
     * @param code     状态码
     * @param message  响应消息
     */
    public static void writeError(HttpServletResponse response, int code, String message) {
        ResultInfo<Object> result = ResultInfo.error(code, message);
        writeJson(response, result);
    }

    /**
     * 获取 ObjectMapper 实例
     *
     * @return ObjectMapper 实例
     */
    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
```

---

## 8. DAO 层

### 8.1 IUserDao.java

```java
package com.example.auth.dao;

import com.example.auth.entity.User;

import java.util.List;

/**
 * 用户数据访问接口
 * 定义用户相关的数据库操作方法
 *
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
public interface IUserDao {

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象，如果不存在返回 null
     */
    User selectById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象，如果不存在返回 null
     */
    User selectByUsername(String username);

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象，如果不存在返回 null
     */
    User selectByEmail(String email);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户对象，如果不存在返回 null
     */
    User selectByPhone(String phone);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    List<User> selectAll();

    /**
     * 分页查询用户
     *
     * @param offset 起始偏移量
     * @param limit  查询数量
     * @return 用户列表
     */
    List<User> selectByPage(int offset, int limit);

    /**
     * 查询用户总数
     *
     * @return 用户总数
     */
    long selectCount();

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

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return true-存在，false-不存在
     */
    boolean existsByEmail(String email);

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return true-存在，false-不存在
     */
    boolean existsByPhone(String phone);
}
```

### 8.2 UserDaoImpl.java

```java
package com.example.auth.dao.impl;

import com.example.auth.dao.IUserDao;
import com.example.auth.entity.User;
import com.example.auth.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 用户数据访问实现类
 * 实现用户相关的数据库操作
 *
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
public class UserDaoImpl implements IUserDao {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户对象，如果不存在返回 null
     */
    @Override
    public User selectById(Long id) {
        logger.debug("根据ID查询用户，ID: {}", id);
        String sql = "SELECT id, username, password, email, phone, status, create_time, update_time " +
                     "FROM t_user WHERE id = ?";
        return DBUtil.queryForObject(sql, User.class, id);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户对象，如果不存在返回 null
     */
    @Override
    public User selectByUsername(String username) {
        logger.debug("根据用户名查询用户，用户名: {}", username);
        String sql = "SELECT id, username, password, email, phone, status, create_time, update_time " +
                     "FROM t_user WHERE username = ?";
        return DBUtil.queryForObject(sql, User.class, username);
    }

    /**
     * 根据邮箱查询用户
     *
     * @param email 邮箱
     * @return 用户对象，如果不存在返回 null
     */
    @Override
    public User selectByEmail(String email) {
        logger.debug("根据邮箱查询用户，邮箱: {}", email);
        String sql = "SELECT id, username, password, email, phone, status, create_time, update_time " +
                     "FROM t_user WHERE email = ?";
        return DBUtil.queryForObject(sql, User.class, email);
    }

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户对象，如果不存在返回 null
     */
    @Override
    public User selectByPhone(String phone) {
        logger.debug("根据手机号查询用户，手机号: {}", phone);
        String sql = "SELECT id, username, password, email, phone, status, create_time, update_time " +
                     "FROM t_user WHERE phone = ?";
        return DBUtil.queryForObject(sql, User.class, phone);
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Override
    public List<User> selectAll() {
        logger.debug("查询所有用户");
        String sql = "SELECT id, username, password, email, phone, status, create_time, update_time " +
                     "FROM t_user ORDER BY create_time DESC";
        return DBUtil.queryForList(sql, User.class);
    }

    /**
     * 分页查询用户
     *
     * @param offset 起始偏移量
     * @param limit  查询数量
     * @return 用户列表
     */
    @Override
    public List<User> selectByPage(int offset, int limit) {
        logger.debug("分页查询用户，偏移量: {}, 数量: {}", offset, limit);
        String sql = "SELECT id, username, password, email, phone, status, create_time, update_time " +
                     "FROM t_user ORDER BY create_time DESC LIMIT ?, ?";
        return DBUtil.queryForList(sql, User.class, offset, limit);
    }

    /**
     * 查询用户总数
     *
     * @return 用户总数
     */
    @Override
    public long selectCount() {
        logger.debug("查询用户总数");
        String sql = "SELECT COUNT(*) FROM t_user";
        Long count = DBUtil.queryForScalar(sql);
        return count != null ? count : 0L;
    }

    /**
     * 插入用户记录
     *
     * @param user 用户对象
     * @return 影响的行数
     */
    @Override
    public int insert(User user) {
        logger.debug("插入用户记录，用户名: {}", user.getUsername());
        String sql = "INSERT INTO t_user (username, password, email, phone, status, create_time, update_time) " +
                     "VALUES (?, ?, ?, ?, ?, NOW(), NOW())";
        return DBUtil.update(sql, 
                user.getUsername(), 
                user.getPassword(), 
                user.getEmail(), 
                user.getPhone(), 
                user.getStatus());
    }

    /**
     * 更新用户记录
     *
     * @param user 用户对象（必须包含ID）
     * @return 影响的行数
     */
    @Override
    public int update(User user) {
        logger.debug("更新用户记录，用户ID: {}", user.getId());
        String sql = "UPDATE t_user SET username = ?, password = ?, email = ?, phone = ?, " +
                     "status = ?, update_time = NOW() WHERE id = ?";
        return DBUtil.update(sql, 
                user.getUsername(), 
                user.getPassword(), 
                user.getEmail(), 
                user.getPhone(), 
                user.getStatus(),
                user.getId());
    }

    /**
     * 删除用户记录
     *
     * @param id 用户ID
     * @return 影响的行数
     */
    @Override
    public int deleteById(Long id) {
        logger.debug("删除用户记录，用户ID: {}", id);
        String sql = "DELETE FROM t_user WHERE id = ?";
        return DBUtil.update(sql, id);
    }

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return true-存在，false-不存在
     */
    @Override
    public boolean existsByUsername(String username) {
        logger.debug("检查用户名是否存在，用户名: {}", username);
        String sql = "SELECT COUNT(*) FROM t_user WHERE username = ?";
        Long count = DBUtil.queryForScalar(sql, username);
        return count != null && count > 0;
    }

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return true-存在，false-不存在
     */
    @Override
    public boolean existsByEmail(String email) {
        logger.debug("检查邮箱是否存在，邮箱: {}", email);
        String sql = "SELECT COUNT(*) FROM t_user WHERE email = ?";
        Long count = DBUtil.queryForScalar(sql, email);
        return count != null && count > 0;
    }

    /**
     * 检查手机号是否存在
     *
     * @param phone 手机号
     * @return true-存在，false-不存在
     */
    @Override
    public boolean existsByPhone(String phone) {
        logger.debug("检查手机号是否存在，手机号: {}", phone);
        String sql = "SELECT COUNT(*) FROM t_user WHERE phone = ?";
        Long count = DBUtil.queryForScalar(sql, phone);
        return count != null && count > 0;
    }
}
```

---

## 9. Service 层

### 9.1 IUserService.java

```java
package com.example.auth.service;

import com.example.auth.entity.User;
import com.example.auth.vo.ResultInfo;

import java.util.List;
import java.util.Map;

/**
 * 用户业务逻辑接口
 * 定义用户相关的业务操作方法
 *
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
public interface IUserService {

    /**
     * 用户登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果，包含用户信息或错误信息
     */
    ResultInfo<User> login(String username, String password);

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @param email    邮箱（可选）
     * @param phone    手机号（可选）
     * @return 注册结果
     */
    ResultInfo<Void> register(String username, String password, String email, String phone);

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    ResultInfo<User> getUserById(Long id);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    ResultInfo<User> getUserByUsername(String username);

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    ResultInfo<List<User>> getAllUsers();

    /**
     * 分页查询用户
     *
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页条数
     * @return 分页结果，包含用户列表和总数
     */
    ResultInfo<Map<String, Object>> getUsersByPage(int pageNum, int pageSize);

    /**
     * 创建用户
     *
     * @param user 用户对象
     * @return 创建结果
     */
    ResultInfo<Void> createUser(User user);

    /**
     * 更新用户信息
     *
     * @param user 用户对象（必须包含ID）
     * @return 更新结果
     */
    ResultInfo<Void> updateUser(User user);

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    ResultInfo<Void> deleteUser(Long id);

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return 检查结果
     */
    ResultInfo<Boolean> checkUsername(String username);

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @return 检查结果
     */
    ResultInfo<Boolean> checkEmail(String email);

    /**
     * 检查手机号是否可用
     *
     * @param phone 手机号
     * @return 检查结果
     */
    ResultInfo<Boolean> checkPhone(String phone);
}
```

### 9.2 UserServiceImpl.java

```java
package com.example.auth.service.impl;

import com.example.auth.constant.Constants;
import com.example.auth.dao.IUserDao;
import com.example.auth.dao.impl.UserDaoImpl;
import com.example.auth.entity.User;
import com.example.auth.enums.ResultCode;
import com.example.auth.service.IUserService;
import com.example.auth.vo.ResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 用户业务逻辑实现类
 * 实现用户相关的业务操作
 *
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
public class UserServiceImpl implements IUserService {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * 用户DAO实例
     */
    private final IUserDao userDao;

    /**
     * 用户名正则模式
     */
    private static final Pattern USERNAME_PATTERN = Pattern.compile(Constants.USERNAME_REGEX);

    /**
     * 密码正则模式
     */
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(Constants.PASSWORD_REGEX);

    /**
     * 邮箱正则模式
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(Constants.EMAIL_REGEX);

    /**
     * 手机号正则模式
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile(Constants.PHONE_REGEX);

    /**
     * 用户状态：启用
     */
    private static final int USER_STATUS_ENABLED = 1;

    /**
     * 用户状态：禁用
     */
    private static final int USER_STATUS_DISABLED = 0;

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
     * @return 登录结果，包含用户信息或错误信息
     */
    @Override
    public ResultInfo<User> login(String username, String password) {
        logger.info("用户登录，用户名: {}", username);

        // 参数校验
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            logger.warn("登录参数为空");
            return ResultInfo.error(ResultCode.USERNAME_PASSWORD_EMPTY);
        }

        // 查询用户
        User user = userDao.selectByUsername(username);
        
        if (user == null) {
            logger.warn("用户不存在，用户名: {}", username);
            return ResultInfo.error(ResultCode.USER_NOT_FOUND);
        }

        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() == USER_STATUS_DISABLED) {
            logger.warn("用户已禁用，用户名: {}", username);
            return ResultInfo.error(ResultCode.FORBIDDEN, "用户已被禁用");
        }

        // 验证密码（实际项目中应使用加密比对）
        if (!password.equals(user.getPassword())) {
            logger.warn("密码错误，用户名: {}", username);
            return ResultInfo.error(ResultCode.PASSWORD_ERROR);
        }

        logger.info("登录成功，用户名: {}", username);
        // 隐藏密码返回
        user.setPassword(null);
        return ResultInfo.success(Constants.MESSAGE_LOGIN_SUCCESS, user);
    }

    /**
     * 用户注册
     *
     * @param username 用户名
     * @param password 密码
     * @param email    邮箱（可选）
     * @param phone    手机号（可选）
     * @return 注册结果
     */
    @Override
    public ResultInfo<Void> register(String username, String password, String email, String phone) {
        logger.info("用户注册，用户名: {}", username);

        // 用户名校验
        if (username == null || username.trim().isEmpty()) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "用户名不能为空");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return ResultInfo.error(ResultCode.USERNAME_FORMAT_ERROR);
        }

        // 密码校验
        if (password == null || password.trim().isEmpty()) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "密码不能为空");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return ResultInfo.error(ResultCode.PASSWORD_FORMAT_ERROR);
        }

        // 邮箱校验（如果提供）
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            return ResultInfo.error(ResultCode.EMAIL_FORMAT_ERROR);
        }

        // 手机号校验（如果提供）
        if (phone != null && !phone.trim().isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            return ResultInfo.error(ResultCode.PHONE_FORMAT_ERROR);
        }

        // 检查用户名是否已存在
        if (userDao.existsByUsername(username)) {
            logger.warn("用户名已存在，用户名: {}", username);
            return ResultInfo.error(ResultCode.USER_EXISTS);
        }

        // 检查邮箱是否已存在
        if (email != null && !email.trim().isEmpty() && userDao.existsByEmail(email)) {
            logger.warn("邮箱已存在，邮箱: {}", email);
            return ResultInfo.error(ResultCode.PARAM_ERROR, "邮箱已被注册");
        }

        // 检查手机号是否已存在
        if (phone != null && !phone.trim().isEmpty() && userDao.existsByPhone(phone)) {
            logger.warn("手机号已存在，手机号: {}", phone);
            return ResultInfo.error(ResultCode.PARAM_ERROR, "手机号已被注册");
        }

        // 创建用户对象（实际项目中密码应加密存储）
        User user = new User();
        user.setUsername(username);
        user.setPassword(password); // 实际应使用 BCrypt 等加密
        user.setEmail(email);
        user.setPhone(phone);
        user.setStatus(USER_STATUS_ENABLED);

        // 插入用户
        int result = userDao.insert(user);
        
        if (result > 0) {
            logger.info("注册成功，用户名: {}", username);
            return ResultInfo.success(Constants.MESSAGE_SUCCESS);
        } else {
            logger.error("注册失败，用户名: {}", username);
            return ResultInfo.error(ResultCode.DB_ERROR);
        }
    }

    /**
     * 根据用户ID查询用户
     *
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public ResultInfo<User> getUserById(Long id) {
        logger.debug("查询用户，ID: {}", id);

        if (id == null || id <= 0) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "用户ID无效");
        }

        User user = userDao.selectById(id);
        
        if (user == null) {
            return ResultInfo.error(ResultCode.USER_NOT_FOUND);
        }

        // 隐藏密码
        user.setPassword(null);
        return ResultInfo.success(user);
    }

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    @Override
    public ResultInfo<User> getUserByUsername(String username) {
        logger.debug("查询用户，用户名: {}", username);

        if (username == null || username.trim().isEmpty()) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "用户名不能为空");
        }

        User user = userDao.selectByUsername(username);
        
        if (user == null) {
            return ResultInfo.error(ResultCode.USER_NOT_FOUND);
        }

        // 隐藏密码
        user.setPassword(null);
        return ResultInfo.success(user);
    }

    /**
     * 查询所有用户
     *
     * @return 用户列表
     */
    @Override
    public ResultInfo<List<User>> getAllUsers() {
        logger.debug("查询所有用户");

        List<User> users = userDao.selectAll();
        
        // 隐藏所有用户密码
        for (User user : users) {
            user.setPassword(null);
        }

        return ResultInfo.success(users);
    }

    /**
     * 分页查询用户
     *
     * @param pageNum  页码（从1开始）
     * @param pageSize 每页条数
     * @return 分页结果，包含用户列表和总数
     */
    @Override
    public ResultInfo<Map<String, Object>> getUsersByPage(int pageNum, int pageSize) {
        logger.debug("分页查询用户，页码: {}, 每页条数: {}", pageNum, pageSize);

        // 参数校验
        if (pageNum <= 0) {
            pageNum = Constants.DEFAULT_PAGE_NUM;
        }
        if (pageSize <= 0) {
            pageSize = Constants.DEFAULT_PAGE_SIZE;
        }
        if (pageSize > Constants.MAX_PAGE_SIZE) {
            pageSize = Constants.MAX_PAGE_SIZE;
        }

        // 计算偏移量
        int offset = (pageNum - 1) * pageSize;

        // 查询数据
        List<User> users = userDao.selectByPage(offset, pageSize);
        long total = userDao.selectCount();

        // 隐藏密码
        for (User user : users) {
            user.setPassword(null);
        }

        // 构建分页结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", users);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        result.put("totalPages", (int) Math.ceil((double) total / pageSize));

        return ResultInfo.success(result);
    }

    /**
     * 创建用户
     *
     * @param user 用户对象
     * @return 创建结果
     */
    @Override
    public ResultInfo<Void> createUser(User user) {
        logger.info("创建用户，用户名: {}", user.getUsername());

        // 参数校验
        if (user == null) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "用户对象不能为空");
        }

        String username = user.getUsername();
        String password = user.getPassword();

        if (username == null || username.trim().isEmpty()) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "用户名不能为空");
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return ResultInfo.error(ResultCode.USERNAME_FORMAT_ERROR);
        }

        if (password == null || password.trim().isEmpty()) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "密码不能为空");
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return ResultInfo.error(ResultCode.PASSWORD_FORMAT_ERROR);
        }

        // 检查邮箱格式（如果提供）
        String email = user.getEmail();
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email).matches()) {
            return ResultInfo.error(ResultCode.EMAIL_FORMAT_ERROR);
        }

        // 检查手机号格式（如果提供）
        String phone = user.getPhone();
        if (phone != null && !phone.trim().isEmpty() && !PHONE_PATTERN.matcher(phone).matches()) {
            return ResultInfo.error(ResultCode.PHONE_FORMAT_ERROR);
        }

        // 检查用户名是否已存在
        if (userDao.existsByUsername(username)) {
            return ResultInfo.error(ResultCode.USER_EXISTS);
        }

        // 设置默认状态
        if (user.getStatus() == null) {
            user.setStatus(USER_STATUS_ENABLED);
        }

        // 插入用户
        int result = userDao.insert(user);
        
        if (result > 0) {
            logger.info("创建用户成功，用户名: {}", username);
            return ResultInfo.success(Constants.MESSAGE_SUCCESS);
        } else {
            logger.error("创建用户失败，用户名: {}", username);
            return ResultInfo.error(ResultCode.DB_ERROR);
        }
    }

    /**
     * 更新用户信息
     *
     * @param user 用户对象（必须包含ID）
     * @return 更新结果
     */
    @Override
    public ResultInfo<Void> updateUser(User user) {
        logger.info("更新用户信息，用户ID: {}", user.getId());

        // 参数校验
        if (user == null) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "用户对象不能为空");
        }
        if (user.getId() == null || user.getId() <= 0) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "用户ID无效");
        }

        // 检查用户是否存在
        User existingUser = userDao.selectById(user.getId());
        if (existingUser == null) {
            return ResultInfo.error(ResultCode.USER_NOT_FOUND);
        }

        // 检查新用户名是否与其他用户冲突
        String username = user.getUsername();
        if (username != null && !username.equals(existingUser.getUsername())) {
            if (!USERNAME_PATTERN.matcher(username).matches()) {
                return ResultInfo.error(ResultCode.USERNAME_FORMAT_ERROR);
            }
            if (userDao.existsByUsername(username)) {
                return ResultInfo.error(ResultCode.USER_EXISTS);
            }
        }

        // 检查邮箱格式（如果提供且变更）
        String email = user.getEmail();
        if (email != null && !email.equals(existingUser.getEmail())) {
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                return ResultInfo.error(ResultCode.EMAIL_FORMAT_ERROR);
            }
            if (userDao.existsByEmail(email)) {
                return ResultInfo.error(ResultCode.PARAM_ERROR, "邮箱已被使用");
            }
        }

        // 检查手机号格式（如果提供且变更）
        String phone = user.getPhone();
        if (phone != null && !phone.equals(existingUser.getPhone())) {
            if (!PHONE_PATTERN.matcher(phone).matches()) {
                return ResultInfo.error(ResultCode.PHONE_FORMAT_ERROR);
            }
            if (userDao.existsByPhone(phone)) {
                return ResultInfo.error(ResultCode.PARAM_ERROR, "手机号已被使用");
            }
        }

        // 更新用户
        int result = userDao.update(user);
        
        if (result > 0) {
            logger.info("更新用户成功，用户ID: {}", user.getId());
            return ResultInfo.success(Constants.MESSAGE_SUCCESS);
        } else {
            logger.error("更新用户失败，用户ID: {}", user.getId());
            return ResultInfo.error(ResultCode.DB_ERROR);
        }
    }

    /**
     * 删除用户
     *
     * @param id 用户ID
     * @return 删除结果
     */
    @Override
    public ResultInfo<Void> deleteUser(Long id) {
        logger.info("删除用户，用户ID: {}", id);

        // 参数校验
        if (id == null || id <= 0) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "用户ID无效");
        }

        // 检查用户是否存在
        User user = userDao.selectById(id);
        if (user == null) {
            return ResultInfo.error(ResultCode.USER_NOT_FOUND);
        }

        // 删除用户
        int result = userDao.deleteById(id);
        
        if (result > 0) {
            logger.info("删除用户成功，用户ID: {}", id);
            return ResultInfo.success(Constants.MESSAGE_SUCCESS);
        } else {
            logger.error("删除用户失败，用户ID: {}", id);
            return ResultInfo.error(ResultCode.DB_ERROR);
        }
    }

    /**
     * 检查用户名是否可用
     *
     * @param username 用户名
     * @return 检查结果
     */
    @Override
    public ResultInfo<Boolean> checkUsername(String username) {
        logger.debug("检查用户名是否可用，用户名: {}", username);

        if (username == null || username.trim().isEmpty()) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "用户名不能为空");
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return ResultInfo.success(false);
        }

        boolean exists = userDao.existsByUsername(username);
        return ResultInfo.success(!exists);
    }

    /**
     * 检查邮箱是否可用
     *
     * @param email 邮箱
     * @return 检查结果
     */
    @Override
    public ResultInfo<Boolean> checkEmail(String email) {
        logger.debug("检查邮箱是否可用，邮箱: {}", email);

        if (email == null || email.trim().isEmpty()) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "邮箱不能为空");
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            return ResultInfo.success(false);
        }

        boolean exists = userDao.existsByEmail(email);
        return ResultInfo.success(!exists);
    }

    /**
     * 检查手机号是否可用
     *
     * @param phone 手机号
     * @return 检查结果
     */
    @Override
    public ResultInfo<Boolean> checkPhone(String phone) {
        logger.debug("检查手机号是否可用，手机号: {}", phone);

        if (phone == null || phone.trim().isEmpty()) {
            return ResultInfo.error(ResultCode.PARAM_ERROR, "手机号不能为空");
        }

        if (!PHONE_PATTERN.matcher(phone).matches()) {
            return ResultInfo.success(false);
        }

        boolean exists = userDao.existsByPhone(phone);
        return ResultInfo.success(!exists);
    }
}
```

---

## 10. Servlet 控制器

### 10.1 UserServlet.java

```java
package com.example.auth.controller;

import com.example.auth.constant.Constants;
import com.example.auth.entity.User;
import com.example.auth.enums.ResultCode;
import com.example.auth.service.IUserService;
import com.example.auth.service.impl.UserServiceImpl;
import com.example.auth.util.JSONUtil;
import com.example.auth.vo.ResultInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * 用户控制器 Servlet
 * 处理用户相关的 HTTP 请求
 *
 * @author SUKE
 * @version 1.0.0
 * @since 2026-05-31
 */
@WebServlet("/api/user/*")
public class UserServlet extends HttpServlet {

    /**
     * 日志记录器
     */
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);

    /**
     * 用户服务实例
     */
    private IUserService userService;

    /**
     * 初始化方法
     */
    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserServiceImpl();
        logger.info("UserServlet 初始化完成");
    }

    /**
     * 处理 GET 请求
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setCharacterEncoding(Constants.CHARSET_UTF8);
        
        String pathInfo = request.getPathInfo();
        logger.debug("GET 请求路径: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 查询所有用户或分页查询
                handleGetUsers(request, response);
            } else if (pathInfo.startsWith("/check/")) {
                // 检查用户名/邮箱/手机号是否可用
                handleCheck(request, response, pathInfo);
            } else {
                // 根据 ID 查询用户
                handleGetUserById(request, response, pathInfo);
            }
        } catch (Exception e) {
            logger.error("处理 GET 请求失败", e);
            JSONUtil.writeError(response, ResultCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * 处理 POST 请求
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setCharacterEncoding(Constants.CHARSET_UTF8);
        
        String pathInfo = request.getPathInfo();
        logger.debug("POST 请求路径: {}", pathInfo);

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // 创建用户
                handleCreateUser(request, response);
            } else if (pathInfo.equals("/login")) {
                // 用户登录
                handleLogin(request, response);
            } else if (pathInfo.equals("/register")) {
                // 用户注册
                handleRegister(request, response);
            } else {
                JSONUtil.writeError(response, ResultCode.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("处理 POST 请求失败", e);
            JSONUtil.writeError(response, ResultCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * 处理 PUT 请求
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setCharacterEncoding(Constants.CHARSET_UTF8);
        
        String pathInfo = request.getPathInfo();
        logger.debug("PUT 请求路径: {}", pathInfo);

        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                // 更新用户信息
                handleUpdateUser(request, response, pathInfo);
            } else {
                JSONUtil.writeError(response, ResultCode.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("处理 PUT 请求失败", e);
            JSONUtil.writeError(response, ResultCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * 处理 DELETE 请求
     *
     * @param request  HTTP 请求对象
     * @param response HTTP 响应对象
     * @throws ServletException Servlet 异常
     * @throws IOException      IO 异常
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setCharacterEncoding(Constants.CHARSET_UTF8);
        
        String pathInfo = request.getPathInfo();
        logger.debug("DELETE 请求路径: {}", pathInfo);

        try {
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                // 删除用户
                handleDeleteUser(request, response, pathInfo);
            } else {
                JSONUtil.writeError(response, ResultCode.NOT_FOUND);
            }
        } catch (Exception e) {
            logger.error("处理 DELETE 请求失败", e);
            JSONUtil.writeError(response, ResultCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * 查询用户列表
     */
    private void handleGetUsers(HttpServletRequest request, HttpServletResponse response) {
        String pageNumStr = request.getParameter(Constants.PARAM_PAGE_NUM);
        String pageSizeStr = request.getParameter(Constants.PARAM_PAGE_SIZE);

        int pageNum = Constants.DEFAULT_PAGE_NUM;
        int pageSize = Constants.DEFAULT_PAGE_SIZE;

        try {
            if (pageNumStr != null && !pageNumStr.isEmpty()) {
                pageNum = Integer.parseInt(pageNumStr);
            }
            if (pageSizeStr != null && !pageSizeStr.isEmpty()) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
        } catch (NumberFormatException e) {
            logger.warn("分页参数格式错误");
        }

        ResultInfo<Map<String, Object>> result = userService.getUsersByPage(pageNum, pageSize);
        JSONUtil.writeJson(response, result);
    }

    /**
     * 根据 ID 查询用户
     */
    private void handleGetUserById(HttpServletRequest request, HttpServletResponse response, String pathInfo) {
        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            ResultInfo<User> result = userService.getUserById(id);
            JSONUtil.writeJson(response, result);
        } catch (NumberFormatException e) {
            logger.warn("用户ID格式错误: {}", pathInfo);
            JSONUtil.writeError(response, ResultCode.PARAM_ERROR, "用户ID格式错误");
        }
    }

    /**
     * 检查用户名/邮箱/手机号是否可用
     */
    private void handleCheck(HttpServletRequest request, HttpServletResponse response, String pathInfo) {
        String param = request.getParameter("value");
        
        if (param == null || param.trim().isEmpty()) {
            JSONUtil.writeError(response, ResultCode.PARAM_ERROR, "参数不能为空");
            return;
        }

        ResultInfo<Boolean> result;
        if (pathInfo.equals("/check/username")) {
            result = userService.checkUsername(param);
        } else if (pathInfo.equals("/check/email")) {
            result = userService.checkEmail(param);
        } else if (pathInfo.equals("/check/phone")) {
            result = userService.checkPhone(param);
        } else {
            JSONUtil.writeError(response, ResultCode.NOT_FOUND);
            return;
        }

        JSONUtil.writeJson(response, result);
    }

    /**
     * 用户登录
     */
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = readRequestBody(request);
        
        if (json == null || json.isEmpty()) {
            JSONUtil.writeError(response, ResultCode.PARAM_ERROR, "请求体不能为空");
            return;
        }

        try {
            Map<String, String> data = JSONUtil.fromJson(json, Map.class);
            String username = data.get(Constants.PARAM_USERNAME);
            String password = data.get(Constants.PARAM_PASSWORD);

            ResultInfo<User> result = userService.login(username, password);
            JSONUtil.writeJson(response, result);
        } catch (Exception e) {
            logger.error("登录处理失败", e);
            JSONUtil.writeError(response, ResultCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * 用户注册
     */
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = readRequestBody(request);
        
        if (json == null || json.isEmpty()) {
            JSONUtil.writeError(response, ResultCode.PARAM_ERROR, "请求体不能为空");
            return;
        }

        try {
            Map<String, String> data = JSONUtil.fromJson(json, Map.class);
            String username = data.get(Constants.PARAM_USERNAME);
            String password = data.get(Constants.PARAM_PASSWORD);
            String email = data.get(Constants.PARAM_EMAIL);
            String phone = data.get(Constants.PARAM_PHONE);

            ResultInfo<Void> result = userService.register(username, password, email, phone);
            JSONUtil.writeJson(response, result);
        } catch (Exception e) {
            logger.error("注册处理失败", e);
            JSONUtil.writeError(response, ResultCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * 创建用户
     */
    private void handleCreateUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String json = readRequestBody(request);
        
        if (json == null || json.isEmpty()) {
            JSONUtil.writeError(response, ResultCode.PARAM_ERROR, "请求体不能为空");
            return;
        }

        try {
            User user = JSONUtil.fromJson(json, User.class);
            ResultInfo<Void> result = userService.createUser(user);
            JSONUtil.writeJson(response, result);
        } catch (Exception e) {
            logger.error("创建用户失败", e);
            JSONUtil.writeError(response, ResultCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * 更新用户
     */
    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response, String pathInfo) 
            throws IOException {
        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            String json = readRequestBody(request);
            
            if (json == null || json.isEmpty()) {
                JSONUtil.writeError(response, ResultCode.PARAM_ERROR, "请求体不能为空");
                return;
            }

            User user = JSONUtil.fromJson(json, User.class);
            user.setId(id);
            ResultInfo<Void> result = userService.updateUser(user);
            JSONUtil.writeJson(response, result);
        } catch (NumberFormatException e) {
            logger.warn("用户ID格式错误: {}", pathInfo);
            JSONUtil.writeError(response, ResultCode.PARAM_ERROR, "用户ID格式错误");
        } catch (Exception e) {
            logger.error("更新用户失败", e);
            JSONUtil.writeError(response, ResultCode.INTERNAL_ERROR, e.getMessage());
        }
    }

    /**
     * 删除用户
     */
    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response, String pathInfo) {
        try {
            Long id = Long.parseLong(pathInfo.substring(1));
            ResultInfo<Void> result = userService.deleteUser(id);
            JSONUtil.writeJson(response, result);
        } catch (NumberFormatException e) {
            logger.warn("用户ID格式错误: {}", pathInfo);
            JSONUtil.writeError(response, ResultCode.PARAM_ERROR, "用户ID格式错误");
        }
    }

    /**
     * 读取请求体内容
     *
     * @param request HTTP 请求对象
     * @return 请求体内容
     * @throws IOException IO 异常
     */
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        return sb.toString();
    }

    /**
     * 销毁方法
     */
    @Override
    public void destroy() {
        super.destroy();
        logger.info("UserServlet 已销毁");
    }
}
```

---

## 11. pom.xml 依赖配置

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>auth-system</artifactId>
    <version>1.0.0</version>
    <packaging>war</packaging>

    <name>通用权限管理系统</name>
    <description>基于 Servlet + MySQL + Druid + DBUtils + Jackson 的权限管理系统</description>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        
        <!-- 依赖版本 -->
        <servlet.version>4.0.1</servlet.version>
        <mysql.version>8.0.33</mysql.version>
        <druid.version>1.2.18</druid.version>
        <dbutils.version>1.7</dbutils.version>
        <jackson.version>2.15.2</jackson.version>
        <slf4j.version>2.0.9</slf4j.version>
        <logback.version>1.4.8</logback.version>
    </properties>

    <dependencies>
        <!-- Servlet API -->
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>${servlet.version}</version>
            <scope>provided</scope>
        </dependency>

        <!-- MySQL 驱动 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>${mysql.version}</version>
        </dependency>

        <!-- Druid 连接池 -->
        <dependency>
            <groupId>com.alibaba</groupId>
            <artifactId>druid</artifactId>
            <version>${druid.version}</version>
        </dependency>

        <!-- Apache DBUtils -->
        <dependency>
            <groupId>commons-dbutils</groupId>
            <artifactId>commons-dbutils</artifactId>
            <version>${dbutils.version}</version>
        </dependency>

        <!-- Jackson -->
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>${jackson.version}</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.datatype</groupId>
            <artifactId>jackson-datatype-jsr310</artifactId>
            <version>${jackson.version}</version>
        </dependency>

        <!-- SLF4J + Logback -->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>${slf4j.version}</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>${logback.version}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>8</source>
                    <target>8</target>
                    <encoding>UTF-8</encoding>
                </configuration>
            </plugin>
            
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.3.2</version>
                <configuration>
                    <failOnMissingWebXml>false</failOnMissingWebXml>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 12. 数据库表结构

### t_user 表

```sql
CREATE TABLE `t_user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID，主键',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `status` TINYINT DEFAULT 1 COMMENT '用户状态：0-禁用，1-启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';
```

---

## 13. API 接口文档

### 13.1 用户登录

| 属性 | 值 |
| :--- | :--- |
| **接口地址** | `POST /api/user/login` |
| **功能描述** | 用户登录，验证用户名和密码 |
| **请求方式** | POST |

**请求体（JSON）**：

| 字段名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `username` | String | 是 | 用户名（4-20位字母、数字、下划线） |
| `password` | String | 是 | 密码（6-32位，至少包含字母和数字） |

**成功响应（JSON）**：

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `code` | int | 状态码，成功为 200 |
| `message` | String | 响应消息 |
| `data` | Object | 用户信息 |
| `data.id` | Long | 用户ID |
| `data.username` | String | 用户名 |
| `data.email` | String | 邮箱 |
| `data.phone` | String | 手机号 |
| `data.status` | Integer | 用户状态 |
| `data.createTime` | String | 创建时间 |
| `data.updateTime` | String | 更新时间 |

**失败响应（JSON）**：

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `code` | int | 状态码 |
| `message` | String | 错误消息 |
| `error` | String | 异常信息（可选） |

**错误码说明**：

| 错误码 | 说明 |
| :--- | :--- |
| 1001 | 用户不存在 |
| 1002 | 密码错误 |
| 1004 | 用户名或密码不能为空 |
| 403 | 用户已被禁用 |

---

### 13.2 用户注册

| 属性 | 值 |
| :--- | :--- |
| **接口地址** | `POST /api/user/register` |
| **功能描述** | 用户注册，创建新用户 |
| **请求方式** | POST |

**请求体（JSON）**：

| 字段名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `username` | String | 是 | 用户名（4-20位字母、数字、下划线） |
| `password` | String | 是 | 密码（6-32位，至少包含字母和数字） |
| `email` | String | 否 | 邮箱 |
| `phone` | String | 否 | 手机号 |

**成功响应（JSON）**：

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `code` | int | 状态码，成功为 200 |
| `message` | String | 响应消息 |
| `data` | null | 无数据 |

**错误码说明**：

| 错误码 | 说明 |
| :--- | :--- |
| 1003 | 用户已存在 |
| 1005 | 用户名格式错误 |
| 1006 | 密码格式错误 |
| 1007 | 邮箱格式错误 |
| 1008 | 手机号格式错误 |

---

### 13.3 查询用户列表

| 属性 | 值 |
| :--- | :--- |
| **接口地址** | `GET /api/user` |
| **功能描述** | 分页查询用户列表 |
| **请求方式** | GET |

**请求参数**：

| 参数名 | 类型 | 必填 | 默认值 | 描述 |
| :--- | :--- | :--- | :--- | :--- |
| `pageNum` | int | 否 | 1 | 页码（从1开始） |
| `pageSize` | int | 否 | 10 | 每页条数（最大100） |

**成功响应（JSON）**：

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `code` | int | 状态码，成功为 200 |
| `message` | String | 响应消息 |
| `data` | Object | 分页结果 |
| `data.list` | Array | 用户列表 |
| `data.total` | Long | 总记录数 |
| `data.pageNum` | int | 当前页码 |
| `data.pageSize` | int | 每页条数 |
| `data.totalPages` | int | 总页数 |

---

### 13.4 查询单个用户

| 属性 | 值 |
| :--- | :--- |
| **接口地址** | `GET /api/user/{id}` |
| **功能描述** | 根据用户ID查询用户信息 |
| **请求方式** | GET |

**路径参数**：

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | Long | 是 | 用户ID |

**成功响应（JSON）**：

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `code` | int | 状态码，成功为 200 |
| `message` | String | 响应消息 |
| `data` | Object | 用户信息 |

**错误码说明**：

| 错误码 | 说明 |
| :--- | :--- |
| 1001 | 用户不存在 |
| 400 | 用户ID无效 |

---

### 13.5 创建用户

| 属性 | 值 |
| :--- | :--- |
| **接口地址** | `POST /api/user` |
| **功能描述** | 创建新用户 |
| **请求方式** | POST |

**请求体（JSON）**：

| 字段名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `username` | String | 是 | 用户名 |
| `password` | String | 是 | 密码 |
| `email` | String | 否 | 邮箱 |
| `phone` | String | 否 | 手机号 |
| `status` | Integer | 否 | 用户状态（默认1-启用） |

---

### 13.6 更新用户

| 属性 | 值 |
| :--- | :--- |
| **接口地址** | `PUT /api/user/{id}` |
| **功能描述** | 更新用户信息 |
| **请求方式** | PUT |

**路径参数**：

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | Long | 是 | 用户ID |

**请求体（JSON）**：

| 字段名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `username` | String | 否 | 用户名 |
| `password` | String | 否 | 密码 |
| `email` | String | 否 | 邮箱 |
| `phone` | String | 否 | 手机号 |
| `status` | Integer | 否 | 用户状态 |

---

### 13.7 删除用户

| 属性 | 值 |
| :--- | :--- |
| **接口地址** | `DELETE /api/user/{id}` |
| **功能描述** | 删除用户 |
| **请求方式** | DELETE |

**路径参数**：

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `id` | Long | 是 | 用户ID |

---

### 13.8 检查用户名是否可用

| 属性 | 值 |
| :--- | :--- |
| **接口地址** | `GET /api/user/check/username` |
| **功能描述** | 检查用户名是否已被注册 |
| **请求方式** | GET |

**请求参数**：

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `value` | String | 是 | 待检查的用户名 |

**成功响应（JSON）**：

| 字段名 | 类型 | 描述 |
| :--- | :--- | :--- |
| `code` | int | 状态码，成功为 200 |
| `message` | String | 响应消息 |
| `data` | Boolean | true-可用，false-不可用 |

---

### 13.9 检查邮箱是否可用

| 属性 | 值 |
| :--- | :--- |
| **接口地址** | `GET /api/user/check/email` |
| **功能描述** | 检查邮箱是否已被注册 |
| **请求方式** | GET |

**请求参数**：

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `value` | String | 是 | 待检查的邮箱 |

---

### 13.10 检查手机号是否可用

| 属性 | 值 |
| :--- | :--- |
| **接口地址** | `GET /api/user/check/phone` |
| **功能描述** | 检查手机号是否已被注册 |
| **请求方式** | GET |

**请求参数**：

| 参数名 | 类型 | 必填 | 描述 |
| :--- | :--- | :--- | :--- |
| `value` | String | 是 | 待检查的手机号 |

---

## 14. 代码审查要点

### 14.1 代码规范检查

| 检查项 | 状态 | 说明 |
| :--- | :--- | :--- |
| 命名规范 | ✅ | 类名使用 PascalCase，方法名和变量名使用 camelCase |
| 代码格式 | ✅ | 使用标准缩进（4空格），代码块清晰 |
| 注释规范 | ✅ | 所有类、方法、常量都有 Javadoc 注释 |
| 魔法值检查 | ✅ | 所有常量值都定义在 Constants 类中 |

### 14.2 安全性检查

| 检查项 | 状态 | 说明 |
| :--- | :--- | :--- |
| SQL 注入防护 | ✅ | 使用 DBUtils 参数化查询 |
| 密码安全 | ⚠️ | 当前为明文存储，建议使用 BCrypt 加密 |
| 参数校验 | ✅ | 所有接口都有参数校验 |
| 异常处理 | ✅ | 所有异常都有统一处理和日志记录 |

### 14.3 性能检查

| 检查项 | 状态 | 说明 |
| :--- | :--- | :--- |
| 循环查询 | ✅ | 无循环中的数据库查询 |
| 连接池配置 | ✅ | 使用 Druid 连接池，配置合理 |
| 分页优化 | ✅ | 使用 LIMIT 分页，避免全表扫描 |

### 14.4 线程安全检查

| 检查项 | 状态 | 说明 |
| :--- | :--- | :--- |
| 单例模式 | ✅ | DBUtil 使用双重检查锁 |
| 静态变量 | ✅ | 线程安全的静态变量 |
| 共享资源 | ✅ | 无线程安全问题 |

---

## 15. 部署说明

### 15.1 环境要求

| 环境 | 版本 |
| :--- | :--- |
| JDK | 8+ |
| MySQL | 8.0+ |
| Maven | 3.6+ |
| Tomcat | 9.0+ |

### 15.2 部署步骤

1. **创建数据库**：
```sql
CREATE DATABASE IF NOT EXISTS gps CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gps;
```

2. **创建用户表**：执行 `t_user.sql`

3. **配置数据库连接**：修改 `src/main/resources/db.properties`

4. **打包项目**：
```bash
mvn clean package
```

5. **部署到 Tomcat**：
   - 将 `target/auth-system-1.0.0.war` 复制到 Tomcat 的 `webapps` 目录
   - 启动 Tomcat

### 15.3 启动验证

访问测试接口：
```bash
# 测试注册
curl -X POST http://localhost:8080/auth-system/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test@123"}'

# 测试登录
curl -X POST http://localhost:8080/auth-system/api/user/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"Test@123"}'
```

---

## 16. 扩展建议

### 16.1 安全增强

- [ ] 添加密码加密（BCrypt）
- [ ] 添加 JWT 令牌认证
- [ ] 添加接口限流
- [ ] 添加请求参数签名校验

### 16.2 功能扩展

- [ ] 添加角色管理
- [ ] 添加权限管理
- [ ] 添加菜单管理
- [ ] 添加日志管理
- [ ] 添加数据字典

### 16.3 性能优化

- [ ] 添加 Redis 缓存
- [ ] 添加接口响应缓存
- [ ] 添加异步任务处理

---

**文档版本**: v1.0.0  
**创建时间**: 2026-05-31  
**作者**: SUKE