package controller;

import com.jfinal.core.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.kit.Ret;
import service.UserService;
import model.User;

import java.util.Map;

public class UserController extends Controller {
    public void HOME() {
        render("/user/HOME.jsp");
    }
    //注册页面
    public void register() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonBody = getRawData();
            Map params = mapper.readValue(jsonBody, Map.class);
            User user = User.dao.doRegister(params);
            if (user.save()) {
                renderJson(Ret.ok("msg", "注册成功").set("data",user));
            } else {
                renderJson(Ret.fail("msg", "用户名或密码错误！"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Ret.fail("msg", "请求参数格式错误，请检查 JSON 格式"));
        }
    }
    //登陆页面
    public void login() {
        //创建 Jackson 的核心对象 ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonBody = getRawData();
            Map params = mapper.readValue(jsonBody, Map.class);
            User user = User.dao.doLogin(params);
            if (user != null) {
                setSessionAttr("currentUser", user);
                renderJson(Ret.ok("msg", "登录成功").set("data", user));
            } else {
                renderJson(Ret.fail("msg", "用户名或密码错误！"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 解析失败通常是因为前端没发 JSON 或者格式不对
            renderJson(Ret.fail("msg", "请求参数格式错误，请检查 JSON 格式"));
        }
    }

    public void logout() {

    }

    public void edit() {

    }
    public void update() {

    }
    public void delete() {

    }

}
