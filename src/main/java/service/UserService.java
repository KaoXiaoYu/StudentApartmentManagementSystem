package service;

import com.jfinal.kit.HashKit;
import com.jfinal.plugin.activerecord.Record;
import model.User;

import java.util.List;

public class UserService {

    public User doLogin(String username, String password) {
        if (username == null || password == null) {
            return null;
        }
        List<Record> recs= User.dao.loadPswd(username);
        if (recs == null || recs.isEmpty()) {
            return null; // 用户不存在
        }
        Record rec = recs.get(0);
        String dbPassword = rec.getStr("password");
        if (dbPassword != null && dbPassword.equals(HashKit.md5(password))) {
            // 密码匹配成功，将数据库查到的完整信息封装到 User 对象中返回
            // 直接将 record 的数据映射给 user
            User user = new User();
            user.put(rec); // JFinal 提供了 put(Map/Record) 方法，可以直接将查询结果放入 Model
            return user;
        }
        return null;
    }
}
