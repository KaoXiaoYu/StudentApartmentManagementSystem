package model;

import com.jfinal.plugin.activerecord.Model;
/**
 * 学院实体模型
 * 映射数据库 college 表，使用JFinal的ActiveRecord持久层
 * 用于封装学院相关数据，并提供数据库CRUD操作能力
 */
public class College extends Model<College> {
    public static final College dao = new College().dao();
}
