package controller;

import base.BaseController;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import exception.BusinessException;
import intercept.ApiExceptionInterceptor;
import intercept.AuthInterceptor;
import service.AccessService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Before({ApiExceptionInterceptor.class, AuthInterceptor.class})
public class OrgController extends BaseController {
    public void collegeList() {
        AccessService.requireCollegeManage(this);
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select college_id, college_name from college where 1 = 1");
        String keyword = text(getPara("keyword"));
        if (!keyword.isBlank()) {
            String like = "%" + keyword + "%";
            sql.append(" and (college_id like ? or college_name like ?)");
            args.add(like);
            args.add(like);
        }
        sql.append(" order by college_id");
        ok("查询成功", Db.find(sql.toString(), args.toArray()));
    }

    public void saveCollege() {
        AccessService.requireCollegeManage(this);
        Map<String, Object> params = body();
        String collegeId = text(params.get("college_id"));
        String collegeName = text(params.get("college_name"));
        validateCollege(collegeId, collegeName);
        Db.update("insert into college(college_id, college_name) values (?, ?) "
                        + "on duplicate key update college_name = values(college_name)",
                collegeId, collegeName);
        ok("教学院信息已保存");
    }

    public void deleteCollege() {
        AccessService.requireCollegeManage(this);
        String collegeId = text(body().get("college_id"));
        if (!collegeId.matches("\\d{2}")) {
            throw new BusinessException(400, "教学院编号必须为 2 位数字");
        }
        if (hasDependencies(collegeId)) {
            throw new BusinessException(400, "该教学院已有班级、宿舍、学生或教师，不能直接删除");
        }
        Db.update("delete from college where college_id = ?", collegeId);
        ok("教学院已删除");
    }

    public void classList() {
        AccessService.Scope scope = AccessService.classManageScope(this);
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select ci.class_id, ci.college_id, c.college_name, "
                + "ci.major_code, ci.major_name, ci.major_short_name, ci.class_code, ci.class_name, "
                + "(select count(*) from student_info s where s.enabled = 1 and s.college_id = ci.college_id "
                + "and s.major_code = ci.major_code and s.class_code = ci.class_code) student_count "
                + "from class_info ci join college c on c.college_id = ci.college_id where 1 = 1");
        if (scope == AccessService.Scope.COLLEGE) {
            sql.append(" and ci.college_id = ?");
            args.add(currentCollegeId());
        } else if (notBlank(getPara("college_id"))) {
            sql.append(" and ci.college_id = ?");
            args.add(getPara("college_id"));
        }
        String keyword = text(getPara("keyword"));
        if (!keyword.isBlank()) {
            String like = "%" + keyword + "%";
            sql.append(" and (c.college_name like ? or ci.college_id like ? or ci.major_code like ? "
                    + "or ci.major_name like ? or ci.major_short_name like ? or ci.class_code like ? "
                    + "or ci.class_name like ?)");
            args.add(like);
            args.add(like);
            args.add(like);
            args.add(like);
            args.add(like);
            args.add(like);
            args.add(like);
        }
        sql.append(" order by ci.college_id, ci.major_code, ci.class_code");
        ok("查询成功", Db.find(sql.toString(), args.toArray()));
    }

    public void saveClass() {
        AccessService.Scope scope = AccessService.classManageScope(this);
        Map<String, Object> params = body();
        String collegeId = text(params.get("college_id"));
        String majorCode = text(params.get("major_code"));
        String majorName = text(params.get("major_name"));
        String majorShortName = text(params.get("major_short_name"));
        String classCode = text(params.get("class_code"));
        String className = text(params.get("class_name"));
        validateClass(collegeId, majorCode, majorName, majorShortName, classCode, className);
        AccessService.requireCollegeScope(this, collegeId, scope);
        if (Db.findById("college", "college_id", collegeId) == null) {
            throw new BusinessException(400, "教学院不存在");
        }
        Db.update("insert into class_info(college_id, major_code, major_name, major_short_name, class_code, class_name) "
                        + "values (?, ?, ?, ?, ?, ?) "
                        + "on duplicate key update major_name = values(major_name), "
                        + "major_short_name = values(major_short_name), class_name = values(class_name)",
                collegeId, majorCode, majorName, majorShortName, classCode, className);
        ok("班级信息已保存");
    }

    public void deleteClass() {
        AccessService.Scope scope = AccessService.classManageScope(this);
        Map<String, Object> params = body();
        String collegeId = text(params.get("college_id"));
        String majorCode = text(params.get("major_code"));
        String classCode = text(params.get("class_code"));
        if (!collegeId.matches("\\d{2}") || !majorCode.matches("\\d{2}") || !classCode.matches("\\d{2}")) {
            throw new BusinessException(400, "教学院编号、专业编号和班级编号必须为 2 位数字");
        }
        AccessService.requireCollegeScope(this, collegeId, scope);
        if (Db.findFirst("select 1 from student_info where enabled = 1 and college_id = ? "
                        + "and major_code = ? and class_code = ? limit 1",
                collegeId, majorCode, classCode) != null) {
            throw new BusinessException(400, "该班级已有学生，不能直接删除");
        }
        Db.update("delete from class_info where college_id = ? and major_code = ? and class_code = ?",
                collegeId, majorCode, classCode);
        ok("班级已删除");
    }

    private boolean hasDependencies(String collegeId) {
        return Db.findFirst("select 1 from class_info where college_id = ? limit 1", collegeId) != null
                || Db.findFirst("select 1 from dorm_info where college_id = ? limit 1", collegeId) != null
                || Db.findFirst("select 1 from student_info where college_id = ? limit 1", collegeId) != null
                || Db.findFirst("select 1 from teacher_info where college_id = ? limit 1", collegeId) != null;
    }

    private void validateCollege(String collegeId, String collegeName) {
        if (!collegeId.matches("\\d{2}")) {
            throw new BusinessException(400, "教学院编号必须为 2 位数字");
        }
        if (collegeName.isBlank() || collegeName.length() > 100) {
            throw new BusinessException(400, "教学院名称不能为空且不能超过 100 字");
        }
    }

    private void validateClass(String collegeId, String majorCode, String majorName,
                               String majorShortName, String classCode, String className) {
        if (!collegeId.matches("\\d{2}") || !majorCode.matches("\\d{2}") || !classCode.matches("\\d{2}")) {
            throw new BusinessException(400, "教学院编号、专业编号和班级编号必须为 2 位数字");
        }
        if (majorName.isBlank() || majorName.length() > 100) {
            throw new BusinessException(400, "专业名称不能为空且不能超过 100 字");
        }
        if (majorShortName.isBlank() || majorShortName.length() > 20) {
            throw new BusinessException(400, "专业简称不能为空且不能超过 20 字");
        }
        if (className.isBlank() || className.length() > 100) {
            throw new BusinessException(400, "班级名称不能为空且不能超过 100 字");
        }
    }

    private String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
