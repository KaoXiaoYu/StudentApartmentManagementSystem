package controller;

import com.jfinal.core.Controller;
import service.UserService;
import com.jfinal.kit.HashKit;
import model.User;

public class UserController extends Controller {
    public void HOME() {
        render("HOME.jsp");
    }
    //登陆页面
    public void loginPage() {
        System.out.println("loginPage 有访问！");
        render("login.jsp");
    }
    public void login() {
        System.out.println("login 有访问！");
        String username = getPara("username");
        String password = getPara("password");

        //从数据库获取相应密码
        UserService userService = new UserService();
        User user = userService.doLogin(username, password);
        if (user != null) {
            setSessionAttr("currentUser", user);
            redirect("/user/HOME");
        } else {
            setAttr("errorMsg", "用户名或密码错误！");
            render("login.jsp");
        }
    }
    public void logout() {

    }
    public void register() {

    }
    public void edit() {

    }
    public void update() {

    }
    public void delete() {

    }

}
