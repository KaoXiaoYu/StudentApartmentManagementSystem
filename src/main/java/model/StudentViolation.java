package model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import exception.BusinessException;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class StudentViolation extends Model<StudentViolation> {
    public static final StudentViolation dao = new StudentViolation();

    public StudentViolation search(Map map){
        String unit_id = map.get("unit_id").toString();
        String room_id = map.get("room_id").toString();
        String time = map.get("time").toString();
        Date now = new Date();
        Date lastAgo;
        if(time=="one_week"){
            lastAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        }else if(time=="two_week"){
            lastAgo = new Date(now.getTime() - 2*7 * 24 * 60 * 60 * 1000);
        }else if(time=="one_month"){
            lastAgo = new Date(now.getTime() - 30L * 24 * 60 * 60 * 1000);
        }else if(time=="three_month"){
            lastAgo = new Date(now.getTime() - 3L*30 * 20 * 60 * 60 * 1000);
        }else if(time=="one_year"){
            lastAgo = new Date(now.getTime() - 12L*30 * 24 * 60 * 60 * 1000);
        }else{
            throw new BusinessException(200,"时间不规范！");
        }

        String sql = "select * from violation_info where unit_id = ? and room_id = ? AND date_time >= ? ORDER BY date_time DESC";
        List<Record> recs = Db.find(sql,unit_id,room_id,lastAgo);

        StudentViolation stuViol = new StudentViolation();
        for(Record rec : recs){
            stuViol.put(rec);
        }
        return stuViol;

    }
}
