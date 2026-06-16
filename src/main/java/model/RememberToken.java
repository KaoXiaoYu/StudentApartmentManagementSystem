package model;

import com.jfinal.plugin.activerecord.Model;
/**
 * 免登录令牌实体类
 * 映射数据库 remember_token 表，存放用户记住登录状态的凭证
 * 基于JFinal ActiveRecord实现数据表增删改查
 */
public class RememberToken extends Model<RememberToken> {
    public static final RememberToken dao = new RememberToken().dao();
}
