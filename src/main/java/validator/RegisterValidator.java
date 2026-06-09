package validator;

import com.jfinal.core.Controller;
import com.jfinal.validate.Validator;

import java.util.HashMap;
import java.util.Map;

public class RegisterValidator extends Validator {

    @Override
    protected void validate(Controller c) {
        validateRequiredString("phone_number", "phone_numberMsg", "电话号码不能为空");
        validateLong("phone_number",11,11,"phone_numberMsg","电话号码必须为11位");

        validateRequiredString("student_id", "student_id", "学号不能为空");
        validateLong("student_id",11,11,"student_idMsg","学号必须为11位");

        validateRequiredString("password", "passwordMsg", "密码不能为空");
        validateLong("password",3,20,"passwordMsg","密码不能短于3位，不能长于20位");

    }

    @Override
    protected void handleError(Controller c) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);
        result.put("msg", c.getAttr("errorMsg")); // 获取你在 validate 里 addError 存进去的提示
        c.renderJson(result);
    }
}
