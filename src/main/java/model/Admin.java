package model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import exception.BusinessException;

import java.util.List;
import java.util.Map;
//未完成
public class Admin extends Model<Admin> {
    public static final Admin dao = new Admin();

    public void doRegister(Map map){
        String phone_number = (String) map.get("phone_number");
        String student_id = (String) map.get("student_id");
        String password = (String) map.get("password");
        String unit_id = (String) map.get("unit_id");
        String room_id = (String) map.get("room_id");
        Admin admin = new Admin();
        String sql = "select * from student_info where ";
        // 注册检测
        List<com.jfinal.plugin.activerecord.Record> rpn= Db.find(sql+"phone_number = ? ",phone_number);
        if (rpn != null && !rpn.isEmpty() && rpn.get(0).getInt("c") > 0) {
            throw new BusinessException(400,"电话号码已注册"); // 标记：手机号已存在
        }
        List<com.jfinal.plugin.activerecord.Record> rsi= Db.find(sql+"student_id = ? ",student_id);
        if (rsi != null && !rsi.isEmpty() && rsi.get(0).getInt("c") > 0) {
            throw new BusinessException(400,"学号注册"); // 标记：学号已存在
        }

        admin.set("phone_number", phone_number);
        admin.set("student_id",student_id);
        admin.set("password", password);
        admin.set("unit_id", unit_id);
        admin.set("room_id", room_id);
        admin.set("permission",1);
        admin.save();
    }

    public Admin doLogin(Map map) {
        String username = (String) map.get("username");
        String password = (String) map.get("password");
        //账号密码长度校验
        if(username.length()!=11){
            throw new BusinessException(200,"电话号码或学号长度必须为11");
        }
        if((password.length()<3)||(password.length()>20)){
            throw new BusinessException(400,"密码长度必须大于三并且小于20");
        }

        boolean isPhoneNumber = ((String) map.get("is_phone_number")).equals("true");
        String sql = "select * from teacher_info where";
        if(isPhoneNumber)sql+=" phone_number = ? ";
        else sql+=" student_id = ? ";
        List<com.jfinal.plugin.activerecord.Record> recs = Db.find(sql,username);

        if (recs == null || recs.isEmpty()) {
            System.out.println("User not exist!");
            return null; // 用户不存在
        }
        Record rec = recs.get(0);
        String dbPassword = rec.getStr("password");
        if (dbPassword != null && dbPassword.equals(password)) {
            // 密码匹配成功，将数据库查到的完整信息封装到 User 对象中返回
            // 直接将 record 的数据映射给 user
            Admin admin = new Admin();
            admin.put(rec); // JFinal 提供了 put(Map/Record) 方法，可以直接将查询结果放入 Model
            return admin;
        }
        return null;
    }
}
