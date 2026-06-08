package model;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.Map;

public class User extends Model<User> {
    public static final User dao = new User();

    public User doRegister(Map map){
        String phone_number = (String) map.get("phone_number");
        String student_id = (String) map.get("student_id");
        String password = (String) map.get("password");
        String unit_id = (String) map.get("unit_id");
        String room_id = (String) map.get("room_id");
        User user = new User();
        user.set("phone_number", phone_number);
        user.set("student_id",student_id);
        user.set("password", password);
        user.set("unit_id", unit_id);
        user.set("room_id", room_id);
        user.set("permission",1);

        //user.save();
        return user;
    }

    public User doLogin(Map map) {
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        boolean isPhoneNumber = ((String) map.get("is_phone_number")).equals("true");
        System.out.println("Received_username: " + username);
        System.out.println("Received_password: " + password);
        if (username == null || password == null) {
            return null;
        }
        List<Record> recs = User.dao.loadPswd(username,isPhoneNumber);
        if (recs == null || recs.isEmpty()) {
            System.out.println("User not exist!");
            return null; // 用户不存在
        }
        Record rec = recs.get(0);
        String dbPassword = rec.getStr("password");
        if (dbPassword != null && dbPassword.equals(password)) {
            // 密码匹配成功，将数据库查到的完整信息封装到 User 对象中返回
            // 直接将 record 的数据映射给 user
            User user = new User();
            user.put(rec); // JFinal 提供了 put(Map/Record) 方法，可以直接将查询结果放入 Model
            return user;
        }
        return null;
    }

    public List<Record> loadPswd(String key,boolean isPhoneNumber){
        String sql = "select * from student_info where";
        if(isPhoneNumber)sql+=" phone_number = ? ";
        else sql+=" student_id = ? ";
        return Db.find(sql,key);
    }

}
