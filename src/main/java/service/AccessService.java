package service;

import com.jfinal.core.Controller;
import exception.BusinessException;

public final class AccessService {
    private AccessService() {
    }

    public static void requireManager(Controller controller) {
        String userType = controller.getSessionAttr("userType");
        String role = controller.getSessionAttr("role");
        if ("teacher".equals(userType) || "cadre".equals(role)) {
            return;
        }
        throw new BusinessException(403, "该功能仅限学生干部或教师");
    }

    public static void requireTeacher(Controller controller) {
        if (!"teacher".equals(controller.getSessionAttr("userType"))) {
            throw new BusinessException(403, "该功能仅限教师");
        }
    }

    public static boolean isSchoolTeacher(Controller controller) {
        return "teacher".equals(controller.getSessionAttr("userType"))
                && "school_teacher".equals(controller.getSessionAttr("role"));
    }

    public static void checkCollege(Controller controller, String collegeId) {
        if (!isSchoolTeacher(controller)
                && !String.valueOf(controller.getSessionAttr("collegeId")).equals(collegeId)) {
            throw new BusinessException(403, "无权管理其他教学院的宿舍");
        }
    }
}
