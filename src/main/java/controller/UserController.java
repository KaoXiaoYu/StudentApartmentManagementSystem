package controller;

import com.jfinal.core.Controller;
import service.UserService;
import com.jfinal.kit.HashKit;
import model.User;

public class UserController extends Controller {
    public void HOME() {
        render("/user/HOME.jsp");
    }
    //登陆页面
    public void loginPage() {
        render("/user/login.jsp");
    }
    public void login() {


        String username = getPara("username");
        String password = getPara("password");
        System.out.println("received_username : "+username);
        System.out.println("received_password : "+password);

        //从数据库获取相应密码
        UserService userService = new UserService();
        User user = userService.doLogin(username, password);
        if (user != null) {
            setSessionAttr("currentUser", user);
            //redirect(getRequest().getContextPath() +"/user/HOME.jsp");
        } else {
            setAttr("errorMsg", "用户名或密码错误！");
            //render("/user/login.jsp");
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
