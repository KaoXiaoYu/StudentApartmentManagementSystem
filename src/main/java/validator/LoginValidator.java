package validator;


import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

public class LoginValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        validateRequiredString("username", "usernameMsg", "请输入邮箱或电话号码!");
        validateRequiredString("password", "passwordMsg", "请输入密码!");
    }

    @Override
    protected void handleError(Controller c) {
        c.keepPara();
        c.render("login.jsp");
    }
}
