package controller;

import base.BaseController;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import exception.BusinessException;
import intercept.ApiExceptionInterceptor;
import intercept.AuthInterceptor;
import service.AccessService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Before({ApiExceptionInterceptor.class, AuthInterceptor.class})
public class AdminController extends BaseController {
    public void teacherList() {
        AccessService.requireTeacherPermissionAdmin(this);
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select t.teacher_id, t.phone_number, t.name, t.role, "
                + "t.college_id, c.college_name, t.class_code, "
                + "coalesce(group_concat(tp.permission_code order by tp.permission_code separator ','), '') permissions "
                + "from teacher_info t left join college c on c.college_id = t.college_id "
                + "left join teacher_permission tp on tp.teacher_id = t.teacher_id "
                + "where t.enabled = 1");
        String keyword = text(getPara("keyword"));
        if (!keyword.isBlank()) {
            String like = "%" + keyword + "%";
            sql.append(" and (t.teacher_id like ? or t.phone_number like ? or t.name like ? "
                    + "or t.role like ? or c.college_name like ? or t.class_code like ?)");
            args.add(like);
            args.add(like);
            args.add(like);
            args.add(like);
            args.add(like);
            args.add(like);
        }
        sql.append(" group by t.teacher_id order by t.role desc, t.teacher_id");
        ok("查询成功", Db.find(sql.toString(), args.toArray()));
    }

    public void teacherPermissionOptions() {
        AccessService.requireTeacherPermissionAdmin(this);
        ok("查询成功", Db.find("select permission_code, permission_name, scope_level from permission_def "
                + "where user_type = 'teacher' and permission_code <> ? order by sort_no",
                AccessService.SYSTEM_MANAGE_TEACHER_PERMISSION));
    }

    public void updateTeacherPermissions() {
        AccessService.requireTeacherPermissionAdmin(this);
        Map<String, Object> params = body();
        String teacherId = text(params.get("teacher_id"));
        Record teacher = Db.findFirst("select teacher_id, role from teacher_info where enabled = 1 and teacher_id = ?",
                teacherId);
        if (teacher == null) {
            throw new BusinessException(404, "教师账号不存在");
        }
        if ("system_admin".equals(teacher.getStr("role"))) {
            throw new BusinessException(400, "系统管理员权限不可在此处修改");
        }
        List<String> permissions = stringList(params.get("permissions"));
        for (String permission : permissions) {
            if (AccessService.SYSTEM_MANAGE_TEACHER_PERMISSION.equals(permission)) {
                throw new BusinessException(403, "不能授予系统管理员权限");
            }
            if (Db.findFirst("select 1 from permission_def where user_type = 'teacher' and permission_code = ?",
                    permission) == null) {
                throw new BusinessException(400, "未知的教师权限：" + permission);
            }
        }
        Db.update("delete from teacher_permission where teacher_id = ?", teacherId);
        for (String permission : permissions) {
            Db.update("insert into teacher_permission(teacher_id, permission_code) values (?, ?)",
                    teacherId, permission);
        }
        ok("教师权限已更新");
    }

    private List<String> stringList(Object value) {
        LinkedHashSet<String> result = new LinkedHashSet<>();
        if (value instanceof List<?>) {
            for (Object item : (List<?>) value) {
                String text = text(item);
                if (!text.isBlank()) {
                    result.add(text);
                }
            }
        }
        return new ArrayList<>(result);
    }

    private String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
