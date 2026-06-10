package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.core.Controller;
import exception.BusinessException;
import model.StudentViolation;

import java.util.HashMap;
import java.util.Map;

public class StudentViolationController extends Controller {
    public void recentList() {//违规情况查询
        //查询的时间为 当周/两周/一个月/三个月/一年 五个档次
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonBody = getRawData();
            Map params = mapper.readValue(jsonBody, Map.class);
            //交给Model类实现
            StudentViolation stuViol = StudentViolation.dao.search(params);
            Map<String,Object> map = new HashMap<>();
            map.put("code",200);
            map.put("msg","查询成功！");
            map.put("data",stuViol);
            renderJson(map);
        } catch (BusinessException e) {
            Map<String,Object> map = new HashMap<>();
            map.put("code",e.getCode());
            map.put("msg",e.getMessage());
            renderJson(map);
        } catch (Exception e) {
            e.fillInStackTrace();
            Map<String,Object> map = new HashMap<>();
            map.put("code", 400);
            map.put("msg", "JSON格式错误");
            renderJson(map);
        }
    }
    public void appeal(){//违规申诉

    }
    public void pendingList(){//申诉列表

    }
    public void audit(){//申诉审核

    }
}
