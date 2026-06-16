package model;
/**
 * 申诉实体类
 * 对应数据库 appeal 数据表，保存各类申诉单据数据
 * 采用 JFinal ActiveRecord 模式，自带数据库增删改查方法
 */
import com.jfinal.plugin.activerecord.Model;

public class Appeal extends Model<Appeal> {
    public static final Appeal dao = new Appeal().dao();
}
