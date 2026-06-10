package model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Record;
import com.mysql.cj.xdevapi.JsonArray;
import exception.BusinessException;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StudentViolation extends Model<StudentViolation> {
    public static final StudentViolation dao = new StudentViolation();

    public List<Record> doSearch(Map map){
        String unit_id = map.get("unit_id").toString();
        if(unit_id.length()!=2){
            throw new BusinessException(400,"楼栋号必须为2位");
        }
        String room_id = map.get("room_id").toString();
        if(room_id.length()!=3){
            throw new BusinessException(400,"寝室号必须为3位");
        }
        String time = map.get("time").toString();
        Date now = new Date();
        Date lastAgo;
        if(time.equals("one_week")){
            lastAgo = new Date(now.getTime() - 7 * 24 * 60 * 60 * 1000);
        }else if(time.equals("two_week")){
            lastAgo = new Date(now.getTime() - 2*7 * 24 * 60 * 60 * 1000);
        }else if(time.equals("one_month")){
            lastAgo = new Date(now.getTime() - 30L * 24 * 60 * 60 * 1000);
        }else if(time.equals("three_month")){
            lastAgo = new Date(now.getTime() - 3L*30 * 20 * 60 * 60 * 1000);
        }else if(time.equals("one_year")){
            lastAgo = new Date(now.getTime() - 12L*30 * 24 * 60 * 60 * 1000);
        }else{
            throw new BusinessException(400,"查询时间不在范围内！");
        }

        String sql = "select * from violation_info where unit_id = ? and room_id = ? AND date_time >= ? ORDER BY date_time DESC";
        return Db.find(sql,unit_id,room_id,lastAgo);
    }
    public void doAppeal(Map map){
        ObjectMapper mapper = new ObjectMapper();
        String violation_id = map.get("violation_id").toString();
        if(violation_id.length()!=8){
            throw new BusinessException(400,"违规id必须长度为8");
        }
        List<Record> recs = Db.find("select * from violation_info where violation_id = ?",violation_id);
        Record rec=recs.get(0);
        System.out.println(rec);
        if(!rec.get("appeal_status").equals("未申诉")){
            throw new BusinessException(400,"该违规正在申诉中，请勿重复申诉");
        }
        String appeal_content = map.get("appeal_content").toString();
        String evidence_urls = map.get("evidence_url").toString();
        System.out.println(evidence_urls);
        String sql = "update violation_info set appeal_status = ?,appeal_time = ?,appeal_content = ?, evidence_urls = ? where violation_id = ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        System.out.println(now);
        Db.update(sql,"申诉中",now,appeal_content,evidence_urls,violation_id);
    }
    public void unAppeal(Map map){

    }
}
