package model;

import com.jfinal.plugin.activerecord.Model;

/**
 * 学生违纪记录实体类
 * 继承 JFinal 的 Model 基类，实现 ActiveRecord 模式，用于操作学生违纪相关的数据库表
 *
 * @param <StudentViolation> 泛型参数，指定当前 Model 对应的实体类型
 */
public class StudentViolation extends Model<StudentViolation> {

    /**
     * 全局单例 DAO 对象
     * 用于直接调用数据库增删改查操作，无需每次手动 new 实例
     * 例如：StudentViolation.dao.findById(id)
     */
    public static final StudentViolation dao = new StudentViolation().dao();
}