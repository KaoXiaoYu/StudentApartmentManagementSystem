package model;
/**
 * 为兼容旧代码导入而保留的实体类
 * 现教师/管理员账号统一使用 Teacher 类，本类仅做向下兼容过渡
 * 无新增属性与业务方法，直接继承 Teacher 的全部逻辑
 */
/**
 * Compatibility model retained for old imports. Teacher accounts now use Teacher.
 */
public class Admin extends Teacher {
}
