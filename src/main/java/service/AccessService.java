package service;

import com.jfinal.core.Controller;
import exception.BusinessException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public final class AccessService {
    public static final String STUDENT_VIEW_DORM = "STUDENT_VIEW_DORM";
    public static final String STUDENT_VIEW_COLLEGE = "STUDENT_VIEW_COLLEGE";
    public static final String STUDENT_VIEW_SCHOOL = "STUDENT_VIEW_SCHOOL";
    public static final String STUDENT_SUBMIT_COLLEGE = "STUDENT_SUBMIT_COLLEGE";
    public static final String STUDENT_SUBMIT_SCHOOL = "STUDENT_SUBMIT_SCHOOL";
    public static final String STUDENT_AUDIT_COLLEGE = "STUDENT_AUDIT_COLLEGE";
    public static final String STUDENT_AUDIT_SCHOOL = "STUDENT_AUDIT_SCHOOL";

    public static final String TEACHER_VIEW_COLLEGE = "TEACHER_VIEW_COLLEGE";
    public static final String TEACHER_VIEW_SCHOOL = "TEACHER_VIEW_SCHOOL";
    public static final String TEACHER_AUDIT_COLLEGE = "TEACHER_AUDIT_COLLEGE";
    public static final String TEACHER_AUDIT_SCHOOL = "TEACHER_AUDIT_SCHOOL";
    public static final String TEACHER_GRANT_STUDENT_COLLEGE = "TEACHER_GRANT_STUDENT_COLLEGE";
    public static final String TEACHER_GRANT_STUDENT_SCHOOL = "TEACHER_GRANT_STUDENT_SCHOOL";

    private AccessService() {
    }

    public enum Scope {
        SELF_DORM, CLASS, DORM, COLLEGE, SCHOOL
    }

    public static void requireTeacher(Controller controller) {
        if (!"teacher".equals(controller.getSessionAttr("userType"))) {
            throw new BusinessException(403, "该功能仅限教师使用");
        }
    }

    public static boolean has(Controller controller, String permission) {
        return permissions(controller).contains(permission);
    }

    public static Set<String> permissions(Controller controller) {
        Object raw = controller.getSessionAttr("permissions");
        if (raw instanceof Collection<?>) {
            Set<String> result = new HashSet<>();
            for (Object item : (Collection<?>) raw) {
                if (item != null) {
                    result.add(String.valueOf(item));
                }
            }
            return result;
        }
        return Set.of();
    }

    public static Scope viewScope(Controller controller) {
        if ("teacher".equals(controller.getSessionAttr("userType"))) {
            if (has(controller, TEACHER_VIEW_SCHOOL) || has(controller, TEACHER_AUDIT_SCHOOL)) {
                return Scope.SCHOOL;
            }
            if (has(controller, TEACHER_VIEW_COLLEGE) || has(controller, TEACHER_AUDIT_COLLEGE)) {
                return Scope.COLLEGE;
            }
            return Scope.CLASS;
        }
        if (has(controller, STUDENT_VIEW_SCHOOL) || has(controller, STUDENT_AUDIT_SCHOOL)) {
            return Scope.SCHOOL;
        }
        if (has(controller, STUDENT_VIEW_COLLEGE) || has(controller, STUDENT_AUDIT_COLLEGE)) {
            return Scope.COLLEGE;
        }
        if (has(controller, STUDENT_VIEW_DORM)) {
            return Scope.DORM;
        }
        return Scope.SELF_DORM;
    }

    public static Scope submitScope(Controller controller) {
        if ("teacher".equals(controller.getSessionAttr("userType"))) {
            Scope scope = viewScope(controller);
            if (scope == Scope.SCHOOL || scope == Scope.COLLEGE) {
                return scope;
            }
            throw new BusinessException(403, "无违规录入权限");
        }
        if (has(controller, STUDENT_SUBMIT_SCHOOL)) {
            return Scope.SCHOOL;
        }
        if (has(controller, STUDENT_SUBMIT_COLLEGE)) {
            return Scope.COLLEGE;
        }
        throw new BusinessException(403, "无违规录入权限");
    }

    public static Scope auditScope(Controller controller) {
        if ("teacher".equals(controller.getSessionAttr("userType"))) {
            if (has(controller, TEACHER_AUDIT_SCHOOL)) {
                return Scope.SCHOOL;
            }
            if (has(controller, TEACHER_AUDIT_COLLEGE)) {
                return Scope.COLLEGE;
            }
        } else {
            if (has(controller, STUDENT_AUDIT_SCHOOL)) {
                return Scope.SCHOOL;
            }
            if (has(controller, STUDENT_AUDIT_COLLEGE)) {
                return Scope.COLLEGE;
            }
        }
        throw new BusinessException(403, "无审核权限");
    }

    public static Scope grantScope(Controller controller) {
        requireTeacher(controller);
        if (has(controller, TEACHER_GRANT_STUDENT_SCHOOL)) {
            return Scope.SCHOOL;
        }
        if (has(controller, TEACHER_GRANT_STUDENT_COLLEGE)) {
            return Scope.COLLEGE;
        }
        throw new BusinessException(403, "无学生授权权限");
    }

    public static void requireCollegeScope(Controller controller, String collegeId, Scope scope) {
        if (scope == Scope.SCHOOL) {
            return;
        }
        String ownCollegeId = controller.getSessionAttr("collegeId");
        if (collegeId == null || !collegeId.equals(ownCollegeId)) {
            throw new BusinessException(403, "无权管理其他教学学院的数据");
        }
    }

    public static boolean canUseStudentPermission(Scope grantScope, String permissionCode) {
        if (grantScope == Scope.SCHOOL) {
            return permissionCode.startsWith("STUDENT_");
        }
        return STUDENT_VIEW_DORM.equals(permissionCode)
                || STUDENT_VIEW_COLLEGE.equals(permissionCode)
                || STUDENT_SUBMIT_COLLEGE.equals(permissionCode)
                || STUDENT_AUDIT_COLLEGE.equals(permissionCode);
    }
}
