package controller;

import base.BaseController;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import exception.BusinessException;
import intercept.ApiExceptionInterceptor;
import intercept.AuthInterceptor;
import service.AccessService;

import java.util.List;
import java.util.Map;

@Before({ApiExceptionInterceptor.class, AuthInterceptor.class})
public class StudentController extends BaseController {
    public void list() {
        AccessService.requireTeacher(this);
        String collegeId = currentCollegeId();
        List<Record> students;
        if (AccessService.isSchoolTeacher(this)) {
            String filterCollege = getPara("college_id");
            students = filterCollege == null || filterCollege.isBlank()
                    ? Db.find("select student_id, phone_number, name, role, college_id, building_no, room_no "
                            + "from student_info where enabled = 1 order by college_id, student_id")
                    : Db.find("select student_id, phone_number, name, role, college_id, building_no, room_no "
                            + "from student_info where enabled = 1 and college_id = ? order by student_id",
                    filterCollege);
        } else {
            students = Db.find("select student_id, phone_number, name, role, college_id, building_no, room_no "
                    + "from student_info where enabled = 1 and college_id = ? order by student_id", collegeId);
        }
        ok("查询成功", students);
    }

    public void authorize() {
        AccessService.requireTeacher(this);
        Map<String, Object> params = body();
        String studentId = text(params.get("student_id"));
        boolean authorized = Boolean.parseBoolean(text(params.get("authorized")));
        Record student = Db.findFirst("select student_id, college_id from student_info where student_id = ?",
                studentId);
        if (student == null) {
            throw new BusinessException(404, "学生账号不存在");
        }
        AccessService.checkCollege(this, student.getStr("college_id"));
        Db.update("update student_info set role = ? where student_id = ?",
                authorized ? "cadre" : "student", studentId);
        ok(authorized ? "已授权为学生干部" : "已撤销学生干部权限");
    }

    private String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
