package controller;

import base.BaseController;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import exception.BusinessException;
import intercept.ApiExceptionInterceptor;
import intercept.AuthInterceptor;
import service.AccessService;
import service.PasswordService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

@Before({ApiExceptionInterceptor.class, AuthInterceptor.class})
public class StudentController extends BaseController {
    public void list() {
        AccessService.requireTeacher(this);
        AccessService.Scope scope = studentManageScope();
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select s.student_id, s.phone_number, s.name, s.role, "
                + "s.college_id, c.college_name, s.major_code, s.study_years, s.class_code, s.serial_no, "
                + "s.building_no, s.room_no, s.first_login_required, "
                + "coalesce(group_concat(sp.permission_code order by sp.permission_code separator ','), '') permissions "
                + "from student_info s join college c on c.college_id = s.college_id "
                + "left join student_permission sp on sp.student_id = s.student_id where s.enabled = 1");
        if (scope == AccessService.Scope.COLLEGE) {
            sql.append(" and s.college_id = ?");
            args.add(currentCollegeId());
        } else if (scope == AccessService.Scope.CLASS) {
            sql.append(" and s.college_id = ? and s.class_code = ?");
            args.add(currentCollegeId());
            args.add(getSessionAttr("classCode"));
        } else if (notBlank(getPara("college_id"))) {
            sql.append(" and s.college_id = ?");
            args.add(getPara("college_id"));
        }
        sql.append(" group by s.student_id order by s.college_id, s.class_code, s.student_id");
        ok("查询成功", Db.find(sql.toString(), args.toArray()));
    }

    public void permissionOptions() {
        AccessService.Scope scope = AccessService.grantScope(this);
        String sql = "select permission_code, permission_name, scope_level from permission_def "
                + "where user_type = 'student' order by sort_no";
        List<Record> all = Db.find(sql);
        List<Record> filtered = new ArrayList<>();
        for (Record item : all) {
            if (AccessService.canUseStudentPermission(scope, item.getStr("permission_code"))) {
                filtered.add(item);
            }
        }
        ok("查询成功", filtered);
    }

    public void updatePermissions() {
        AccessService.Scope scope = AccessService.grantScope(this);
        Map<String, Object> params = body();
        String studentId = text(params.get("student_id"));
        Record student = Db.findFirst("select student_id, college_id from student_info where enabled = 1 and student_id = ?",
                studentId);
        if (student == null) {
            throw new BusinessException(404, "学生账号不存在");
        }
        AccessService.requireCollegeScope(this, student.getStr("college_id"), scope);
        List<String> permissions = stringList(params.get("permissions"));
        for (String permission : permissions) {
            if (!AccessService.canUseStudentPermission(scope, permission)) {
                throw new BusinessException(403, "不能授予超出自身授权范围的学生权限");
            }
            if (Db.findFirst("select 1 from permission_def where user_type = 'student' and permission_code = ?",
                    permission) == null) {
                throw new BusinessException(400, "未知的学生权限：" + permission);
            }
        }
        Db.update("delete from student_permission where student_id = ?", studentId);
        for (String permission : permissions) {
            Db.update("insert into student_permission(student_id, permission_code) values (?, ?)",
                    studentId, permission);
        }
        ok("学生权限已更新");
    }

    public void importStudents() {
        AccessService.requireTeacher(this);
        Map<String, Object> params = body();
        List<Map<String, Object>> rows = mapList(params.get("students"));
        if (rows.isEmpty()) {
            throw new BusinessException(400, "请提供要导入的学生列表");
        }
        AccessService.Scope scope = studentManageScope();
        int count = 0;
        for (Map<String, Object> row : rows) {
            StudentNo no = parseStudentNo(text(row.get("student_id")));
            AccessService.requireCollegeScope(this, no.collegeId, scope);
            if (scope == AccessService.Scope.CLASS && !no.classCode.equals(getSessionAttr("classCode"))) {
                throw new BusinessException(403, "基础教师只能导入自己班级的学生");
            }
            String name = text(row.get("name"));
            if (name.isBlank() || name.length() > 50) {
                throw new BusinessException(400, "学生姓名不能为空且不能超过 50 字");
            }
            ensureCollege(no.collegeId);
            Db.update("insert into student_info(student_id, name, password_hash, role, college_id, major_code, "
                            + "study_years, class_code, serial_no, first_login_required, enabled, created_at) "
                            + "values (?, ?, ?, 'student', ?, ?, ?, ?, ?, 1, 1, now()) "
                            + "on duplicate key update name = values(name), college_id = values(college_id), "
                            + "major_code = values(major_code), study_years = values(study_years), "
                            + "class_code = values(class_code), serial_no = values(serial_no), enabled = 1",
                    no.studentId, name, PasswordService.hash("123456"), no.collegeId, no.majorCode,
                    no.studyYears, no.classCode, no.serialNo);
            count++;
        }
        ok("成功导入 " + count + " 名学生");
    }

    public void bindProfile() {
        if (!"student".equals(currentUserType())) {
            throw new BusinessException(403, "仅学生需要绑定个人信息");
        }
        Map<String, Object> params = body();
        String phone = text(params.get("phone_number"));
        String buildingNo = text(params.get("building_no"));
        String roomNo = text(params.get("room_no"));
        if (!phone.matches("1\\d{10}")) {
            throw new BusinessException(400, "手机号必须为 11 位大陆手机号");
        }
        validateDorm(buildingNo, roomNo);
        if (Db.findFirst("select 1 from dorm_info where college_id = ? and building_no = ? and room_no = ?",
                currentCollegeId(), buildingNo, roomNo) == null) {
            Db.update("insert into dorm_info(college_id, building_no, room_no) values (?, ?, ?)",
                    currentCollegeId(), buildingNo, roomNo);
        }
        Record used = Db.findFirst("select student_id from student_info where phone_number = ? and student_id <> ?",
                phone, currentUserId());
        if (used != null) {
            throw new BusinessException(400, "该手机号已被其他学生绑定");
        }
        Db.update("update student_info set phone_number = ?, building_no = ?, room_no = ?, "
                        + "first_login_required = 0 where student_id = ?",
                phone, buildingNo, roomNo, currentUserId());
        setSessionAttr("buildingNo", buildingNo);
        setSessionAttr("roomNo", roomNo);
        setSessionAttr("firstLoginRequired", 0);
        ok("绑定成功");
    }

    public void dormMembers() {
        AccessService.requireTeacher(this);
        AccessService.Scope scope = studentManageScope();
        String collegeId = text(getPara("college_id"));
        String buildingNo = text(getPara("building_no"));
        String roomNo = text(getPara("room_no"));
        validateDorm(buildingNo, roomNo);
        if (collegeId.isBlank()) {
            collegeId = currentCollegeId();
        }
        AccessService.requireCollegeScope(this, collegeId, scope);
        if (scope == AccessService.Scope.CLASS) {
            ok("查询成功", Db.find("select student_id, name, phone_number, college_id, class_code, building_no, room_no "
                            + "from student_info where enabled = 1 and college_id = ? and class_code = ? "
                            + "and building_no = ? and room_no = ? order by student_id",
                    collegeId, getSessionAttr("classCode"), buildingNo, roomNo));
            return;
        }
        ok("查询成功", Db.find("select student_id, name, phone_number, college_id, class_code, building_no, room_no "
                        + "from student_info where enabled = 1 and college_id = ? and building_no = ? and room_no = ? "
                        + "order by student_id",
                collegeId, buildingNo, roomNo));
    }

    public void updateDorm() {
        AccessService.requireTeacher(this);
        AccessService.Scope scope = studentManageScope();
        Map<String, Object> params = body();
        String studentId = text(params.get("student_id"));
        String buildingNo = text(params.get("building_no"));
        String roomNo = text(params.get("room_no"));
        validateDorm(buildingNo, roomNo);
        Record student = Db.findFirst("select student_id, college_id, class_code from student_info where enabled = 1 and student_id = ?",
                studentId);
        if (student == null) {
            throw new BusinessException(404, "学生账号不存在");
        }
        AccessService.requireCollegeScope(this, student.getStr("college_id"), scope);
        if (scope == AccessService.Scope.CLASS && !student.getStr("class_code").equals(getSessionAttr("classCode"))) {
            throw new BusinessException(403, "基础教师只能调整自己班级学生的宿舍");
        }
        if (Db.findFirst("select 1 from dorm_info where college_id = ? and building_no = ? and room_no = ?",
                student.getStr("college_id"), buildingNo, roomNo) == null) {
            Db.update("insert into dorm_info(college_id, building_no, room_no) values (?, ?, ?)",
                    student.getStr("college_id"), buildingNo, roomNo);
        }
        Db.update("update student_info set building_no = ?, room_no = ? where student_id = ?",
                buildingNo, roomNo, studentId);
        ok("宿舍成员信息已更新");
    }

    private AccessService.Scope studentManageScope() {
        if (AccessService.has(this, AccessService.TEACHER_GRANT_STUDENT_SCHOOL)
                || AccessService.has(this, AccessService.TEACHER_VIEW_SCHOOL)) {
            return AccessService.Scope.SCHOOL;
        }
        if (AccessService.has(this, AccessService.TEACHER_GRANT_STUDENT_COLLEGE)
                || AccessService.has(this, AccessService.TEACHER_VIEW_COLLEGE)) {
            return AccessService.Scope.COLLEGE;
        }
        return AccessService.Scope.CLASS;
    }

    private void ensureCollege(String collegeId) {
        if (Db.findById("college", "college_id", collegeId) == null) {
            Db.update("insert into college(college_id, college_name) values (?, ?)",
                    collegeId, "学院 " + collegeId);
        }
    }

    private StudentNo parseStudentNo(String studentId) {
        if (!studentId.matches("\\d{11}")) {
            throw new BusinessException(400, "学号必须为 11 位数字");
        }
        return new StudentNo(studentId, studentId.substring(2, 4), studentId.substring(4, 6),
                studentId.substring(6, 7), studentId.substring(7, 9), studentId.substring(9, 11));
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

    private List<Map<String, Object>> mapList(Object value) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (value instanceof List<?>) {
            for (Object item : (List<?>) value) {
                if (item instanceof Map<?, ?> raw) {
                    Map<String, Object> mapped = new java.util.LinkedHashMap<>();
                    for (Map.Entry<?, ?> entry : raw.entrySet()) {
                        mapped.put(String.valueOf(entry.getKey()), entry.getValue());
                    }
                    result.add(mapped);
                }
            }
        }
        return result;
    }

    private void validateDorm(String buildingNo, String roomNo) {
        if (buildingNo == null || !buildingNo.matches("\\d{2}")) {
            throw new BusinessException(400, "楼栋号必须为 2 位数字");
        }
        if (roomNo == null || !roomNo.matches("\\d{3}")) {
            throw new BusinessException(400, "房间号必须为 3 位数字");
        }
    }

    private String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }

    private record StudentNo(String studentId, String collegeId, String majorCode, String studyYears,
                             String classCode, String serialNo) {
    }
}
