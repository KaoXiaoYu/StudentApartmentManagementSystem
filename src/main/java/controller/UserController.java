package controller;

import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.kit.Ret;
import exception.BusinessException;
import service.UserService;
import model.User;
import validator.LoginValidator;
import validator.RegisterValidator;

import java.util.Map;

public class UserController extends Controller {
    public void HOME() {
        render("/user/HOME.jsp");
    }
    //注册页面
    @Before(RegisterValidator.class)
    public void register() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonBody = getRawData();
            Map params = mapper.readValue(jsonBody, Map.class);
            User.dao.doRegister(params);
            renderJson(Ret.ok("msg","注册成功"));
        } catch (BusinessException e) {
            // 捕获我们刚才抛出的业务异常
            renderJson(Ret.fail("msg", e.getMessage()).set("code", e.getCode()));
        }catch (Exception e) {
            e.printStackTrace();
            renderJson(Ret.fail("msg", "请求参数格式错误，请检查 JSON 格式"));
        }
    }
    //登陆页面
    @Before(LoginValidator.class)
    public void login() {
        //创建 Jackson 的核心对象 ObjectMapper
        ObjectMapper mapper = new ObjectMapper();
        try {
            String jsonBody = getRawData();
            Map params = mapper.readValue(jsonBody, Map.class);
            User user = User.dao.doLogin(params);
            if (user != null) {
                renderJson(new java.util.HashMap<String, Object>() {
                    {
                        put("code", 200);
                        put("msg", "登录成功");
                        put("data", new java.util.HashMap<String, Object>() {{
                            put("token", "some-jwt-token-or-session-id"); // 返回给前端的凭证
                        }});
                    put("msg", "登录成功");
                    put("data", user);
                    }});
            } else {
                renderJson(Ret.fail("msg", "用户名或密码错误！"));
            }
        } catch (Exception e) {
            e.printStackTrace();
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
