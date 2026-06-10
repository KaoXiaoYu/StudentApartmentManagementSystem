package controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import exception.BusinessException;
import model.User;

import java.util.HashMap;
import java.util.Map;

public class UserController extends Controller {

    //注册页面
    public void register() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonBody = getRawData();
            Map params = mapper.readValue(jsonBody, Map.class);
            User.dao.doRegister(params);
            Map<String,Object> map = new HashMap<>();
            map.put("code",200);
            map.put("msg","注册成功！");
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
    //登陆页面
    public void login() {
        ObjectMapper mapper = new ObjectMapper();
        try {//正常逻辑
            String jsonBody = getRawData();
            Map params = mapper.readValue(jsonBody, Map.class);
            User user = User.dao.doLogin(params);
            if (user != null) {
                Map<String,Object> map = new HashMap<>();
                map.put("code",200);
                map.put("msg","登录成功");
                map.put("data",user);
                renderJson(map);
            } else {
                Map<String,Object> map = new HashMap<>();
                map.put("code",400);
                map.put("msg","账号或密码错误");
                renderJson(map);
            }
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
    public void logout() {

    }


}
