package controller;

import base.BaseController;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import intercept.ApiExceptionInterceptor;
import intercept.AuthInterceptor;
import service.AccessService;

@Before({ApiExceptionInterceptor.class, AuthInterceptor.class})
public class GlobalController extends BaseController {
    public void colleges() {
        if (AccessService.viewScope(this) == AccessService.Scope.SCHOOL
                || AccessService.has(this, AccessService.TEACHER_GRANT_STUDENT_SCHOOL)) {
            ok("查询成功", Db.find("select college_id, college_name from college order by college_id"));
            return;
        }
        ok("查询成功", Db.find("select college_id, college_name from college where college_id = ?",
                currentCollegeId()));
    }

    public void dorms() {
        if (AccessService.viewScope(this) == AccessService.Scope.SCHOOL) {
            String collegeId = getPara("college_id");
            if (collegeId == null || collegeId.isBlank()) {
                ok("查询成功", Db.find("select college_id, building_no, room_no from dorm_info "
                        + "order by college_id, building_no, room_no"));
            } else {
                ok("查询成功", Db.find("select college_id, building_no, room_no from dorm_info "
                        + "where college_id = ? order by building_no, room_no", collegeId));
            }
            return;
        }
        ok("查询成功", Db.find("select college_id, building_no, room_no from dorm_info "
                + "where college_id = ? order by building_no, room_no", currentCollegeId()));
    }
}
