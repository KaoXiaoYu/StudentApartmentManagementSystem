package model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;

import java.util.List;

public class User extends Model<User> {
    public static final User dao = new User();

    public List<Record> loadPswd(String key){
        String sql = "select * from student_info where";
        if(key.length()==11)sql+=" phone_number = ? ";
        else sql+="student_id = ? ";
        return Db.find(sql,key);
    }

}
