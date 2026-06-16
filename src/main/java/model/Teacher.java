package model;

import com.jfinal.plugin.activerecord.Model;

/**
 * 教师实体类
 * 继承 JFinal 的 Model 基类，采用 ActiveRecord 模式，用于操作教师相关的数据库表
 *
 * @param <Teacher> 泛型参数，指定当前 Model 对应的实体类型
 */
public class Teacher extends Model<Teacher> {

    /**
     * 全局单例 DAO 对象
     * 用于直接调用数据库增删改查操作，无需每次手动 new 实例
     * 例如：Teacher.dao.findById(id)
     */
    public static final Teacher dao = new Teacher().dao();
}