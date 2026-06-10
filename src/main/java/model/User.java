package model;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;
import java.util.Map;

import com.jfinal.core.Controller;
import exception.BusinessException;

public class User extends Model<User> {
    public static final User dao = new User();

    public void doRegister(Map map){
        String phone_number = map.get("phone_number").toString();
        String student_id = map.get("student_id").toString();
        String password = map.get("password").toString();
        String unit_id = map.get("unit_id").toString();
        String room_id = map.get("room_id").toString();
        User user = new User();
        String sql = "select * from student_info where ";
        // 注册检测
        List<Record> rpn= Db.find(sql+"phone_number = ? ",phone_number);
        if (rpn != null && !rpn.isEmpty() && rpn.get(0).getInt("c") > 0) {
            throw new BusinessException(400,"电话号码已注册"); // 标记：手机号已存在
        }
        List<Record> rsi= Db.find(sql+"student_id = ? ",student_id);
        if (rsi != null && !rsi.isEmpty() && rsi.get(0).getInt("c") > 0) {
            throw new BusinessException(400,"学号已经注册"); // 标记：学号已存在
        }

        user.set("phone_number", phone_number);
        user.set("student_id",student_id);
        user.set("password", password);
        user.set("unit_id", unit_id);
        user.set("room_id", room_id);
        user.set("permission",1);
        user.save();
    }

    public Record doLogin(Map map) {
        String username = map.get("username").toString();
        String password = map.get("password").toString();
        //账号密码长度校验
        if(username.length()!=11){
            throw new BusinessException(400,"电话号码或学号长度必须为11");
        }
        if((password.length()<3)||(password.length()>20)){
            throw new BusinessException(400,"密码长度必须大于三并且小于20");
        }

        boolean isPhoneNumber = (map.get("is_phone_number").toString()).equals("true");
        String sql = "select * from student_info where";
        if(isPhoneNumber)sql+=" phone_number = ? ";
        else sql+=" student_id = ? ";
        List<Record> recs = Db.find(sql,username);

        if (recs == null || recs.isEmpty()) {
            System.out.println("User not exist!");
            return null; // 用户不存在
        }
        Record rec = recs.get(0);
        String dbPassword = rec.getStr("password");
        if (dbPassword != null && dbPassword.equals(password)) {
            return rec;
        }
        return null;
    }


}
