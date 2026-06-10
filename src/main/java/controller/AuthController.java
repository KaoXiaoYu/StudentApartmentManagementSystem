package controller;

import base.BaseController;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Record;
import intercept.ApiExceptionInterceptor;
import service.AuthService;

@Before(ApiExceptionInterceptor.class)
public class AuthController extends BaseController {
    private final AuthService authService = new AuthService();

    public void login() {
        Record user = authService.login(this, body());
        ok("登录成功", user);
    }

    public void current() {
        Record user = authService.currentUser(this);
        if (user == null) {
            getResponse().setStatus(401);
            renderJson(java.util.Map.of("code", 401, "msg", "未登录"));
            return;
        }
        ok("已登录", user);
    }

    public void logout() {
        authService.logout(this);
        ok("已退出登录");
    }
}
