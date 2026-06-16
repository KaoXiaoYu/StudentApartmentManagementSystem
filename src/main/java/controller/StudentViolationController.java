package controller;

import base.BaseController;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import exception.BusinessException;
import intercept.ApiExceptionInterceptor;
import intercept.AuthInterceptor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import service.AccessService;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Before({ApiExceptionInterceptor.class, AuthInterceptor.class})
public class StudentViolationController extends BaseController {
    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public void list() {
        requireBoundIfStudent();
        List<Object> args = new ArrayList<>();
        StringBuilder sql = baseViolationSql();
        applyViewScope(sql, args);
        applyDormFilter(sql, args);
        sql.append(" order by v.occurred_at desc");
        ok("查询成功", Db.find(sql.toString(), args.toArray()));
    }

    public void recentList() {
        list();
    }

    public void add() {
        AccessService.Scope scope = AccessService.submitScope(this);
        Map<String, Object> params = body();
        insertViolation(params, scope);
        ok("违规信息已录入");
    }

    public void appeal() {
        if (!"student".equals(currentUserType())) {
            throw new BusinessException(403, "只有学生可以提交申诉");
        }
        requireBoundDorm();
        Map<String, Object> params = body();
        String violationId = text(params.get("violation_id"));
        String reason = text(params.get("reason"));
        if (reason.length() < 5 || reason.length() > 1000) {
            throw new BusinessException(400, "申诉理由长度应为 5 到 1000 字");
        }
        Record violation = Db.findFirst("select * from violation_info where violation_id = ?", violationId);
        if (violation == null) {
            throw new BusinessException(404, "违规记录不存在");
        }
        if (!currentCollegeId().equals(violation.getStr("college_id"))
                || !getSessionAttr("buildingNo").equals(violation.getStr("building_no"))
                || !getSessionAttr("roomNo").equals(violation.getStr("room_no"))) {
            throw new BusinessException(403, "只能申诉自己宿舍的违规记录");
        }
        if (Db.findFirst("select appeal_id from appeal_info where violation_id = ? and status = 'pending'",
                violationId) != null) {
            throw new BusinessException(400, "该违规记录已有待审核申诉");
        }
        Db.update("insert into appeal_info(appeal_id, violation_id, student_id, reason, status, created_at) "
                        + "values (?, ?, ?, ?, 'pending', now())",
                id(), violationId, currentUserId(), reason);
        ok("申诉已提交");
    }

    public void appealList() {
        AccessService.Scope scope = AccessService.auditScope(this);
        List<Object> args = new ArrayList<>();
        StringBuilder sql = new StringBuilder("select a.appeal_id, a.violation_id, a.student_id, s.name student_name, "
                + "a.reason, a.status, a.audit_reply, a.created_at, a.audited_at, v.college_id, v.building_no, "
                + "v.room_no, v.violation_type, v.description, v.occurred_at "
                + "from appeal_info a join violation_info v on v.violation_id = a.violation_id "
                + "join student_info s on s.student_id = a.student_id where 1 = 1");
        if (scope == AccessService.Scope.COLLEGE) {
            sql.append(" and v.college_id = ?");
            args.add(currentCollegeId());
        }
        sql.append(" order by a.created_at desc");
        ok("查询成功", Db.find(sql.toString(), args.toArray()));
    }

    public void audit() {
        AccessService.Scope scope = AccessService.auditScope(this);
        Map<String, Object> params = body();
        String appealId = text(params.get("appeal_id"));
        String status = text(params.get("status"));
        String reply = text(params.get("reply"));
        if (!"approved".equals(status) && !"rejected".equals(status)) {
            throw new BusinessException(400, "审核结果只能为 approved 或 rejected");
        }
        if (reply.isBlank()) {
            throw new BusinessException(400, "请填写审核意见");
        }
        Record appeal = Db.findFirst("select a.status, v.college_id from appeal_info a "
                + "join violation_info v on v.violation_id = a.violation_id where a.appeal_id = ?", appealId);
        if (appeal == null) {
            throw new BusinessException(404, "申诉记录不存在");
        }
        AccessService.requireCollegeScope(this, appeal.getStr("college_id"), scope);
        if (!"pending".equals(appeal.getStr("status"))) {
            throw new BusinessException(400, "该申诉已经审核");
        }
        Db.update("update appeal_info set status = ?, audit_reply = ?, audited_by = ?, audited_at = now() "
                + "where appeal_id = ?", status, reply, currentUserId(), appealId);
        ok("审核完成");
    }

    public void importExcel() {
        AccessService.Scope scope = AccessService.submitScope(this);
        UploadFile upload = getFile("file");
        if (upload == null) {
            throw new BusinessException(400, "请选择 Excel 文件");
        }
        File file = upload.getFile();
        int count = 0;
        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            DataFormatter formatter = new DataFormatter();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null || value(formatter, row.getCell(0)).isBlank()) {
                    continue;
                }
                Map<String, Object> item = Map.of(
                        "college_id", value(formatter, row.getCell(0)),
                        "building_no", value(formatter, row.getCell(1)),
                        "room_no", value(formatter, row.getCell(2)),
                        "violation_type", value(formatter, row.getCell(3)),
                        "description", value(formatter, row.getCell(4)),
                        "occurred_at", value(formatter, row.getCell(5))
                );
                insertViolation(item, scope);
                count++;
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(400, "Excel 读取失败，请使用系统模板");
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
        ok("成功导入 " + count + " 条违规记录");
    }

    public void exportWeek() {
        AccessService.Scope scope = AccessService.viewScope(this);
        LocalDate monday = LocalDate.now().with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY));
        LocalDateTime start = monday.atStartOfDay();
        LocalDateTime end = monday.plusDays(7).atStartOfDay();
        List<Object> args = new ArrayList<>();
        args.add(Timestamp.valueOf(start));
        args.add(Timestamp.valueOf(end));
        StringBuilder sql = new StringBuilder("select c.college_name, v.building_no, v.room_no, v.violation_type, "
                + "v.description, v.occurred_at, v.created_by_name from violation_info v "
                + "join college c on c.college_id = v.college_id where v.occurred_at >= ? and v.occurred_at < ?");
        appendScopeCondition(sql, args, scope, "v");
        sql.append(" order by v.college_id, v.building_no, v.room_no");
        List<Record> records = Db.find(sql.toString(), args.toArray());

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("本周违规汇总");
            String[] headers = {"教学学院", "楼栋号", "房间号", "违规类型", "情况说明", "发生时间", "录入人"};
            Row header = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                header.createCell(i).setCellValue(headers[i]);
                sheet.setColumnWidth(i, i == 4 ? 12000 : 4500);
            }
            for (int i = 0; i < records.size(); i++) {
                Record record = records.get(i);
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue(record.getStr("college_name"));
                row.createCell(1).setCellValue(record.getStr("building_no"));
                row.createCell(2).setCellValue(record.getStr("room_no"));
                row.createCell(3).setCellValue(record.getStr("violation_type"));
                row.createCell(4).setCellValue(record.getStr("description"));
                row.createCell(5).setCellValue(String.valueOf(record.get("occurred_at")));
                row.createCell(6).setCellValue(record.getStr("created_by_name"));
            }
            File output = File.createTempFile("sams-weekly-", ".xlsx");
            try (FileOutputStream stream = new FileOutputStream(output)) {
                workbook.write(stream);
            }
            renderFile(output, "宿舍违规周汇总-" + monday + ".xlsx");
        } catch (Exception e) {
            throw new BusinessException(500, "汇总表导出失败");
        }
    }

    private StringBuilder baseViolationSql() {
        return new StringBuilder("select v.violation_id, v.college_id, c.college_name, "
                + "v.building_no, v.room_no, v.violation_type, v.description, v.occurred_at, "
                + "v.created_by_name, v.created_at, "
                + "(select a.status from appeal_info a where a.violation_id = v.violation_id "
                + "order by a.created_at desc limit 1) appeal_status "
                + "from violation_info v join college c on c.college_id = v.college_id where 1 = 1");
    }

    private void applyViewScope(StringBuilder sql, List<Object> args) {
        appendScopeCondition(sql, args, AccessService.viewScope(this), "v");
        if (AccessService.viewScope(this) == AccessService.Scope.SCHOOL && notBlank(getPara("college_id"))) {
            sql.append(" and v.college_id = ?");
            args.add(getPara("college_id"));
        }
    }

    private void appendScopeCondition(StringBuilder sql, List<Object> args, AccessService.Scope scope, String alias) {
        if (scope == AccessService.Scope.SCHOOL) {
            return;
        }
        if (scope == AccessService.Scope.COLLEGE) {
            sql.append(" and ").append(alias).append(".college_id = ?");
            args.add(currentCollegeId());
            return;
        }
        if (scope == AccessService.Scope.CLASS) {
            sql.append(" and exists (select 1 from student_info s where s.enabled = 1 ")
                    .append("and s.college_id = ").append(alias).append(".college_id ")
                    .append("and s.building_no = ").append(alias).append(".building_no ")
                    .append("and s.room_no = ").append(alias).append(".room_no ")
                    .append("and s.college_id = ? and s.class_code = ?)");
            args.add(currentCollegeId());
            args.add(getSessionAttr("classCode"));
            return;
        }
        sql.append(" and ").append(alias).append(".college_id = ? and ")
                .append(alias).append(".building_no = ? and ").append(alias).append(".room_no = ?");
        args.add(currentCollegeId());
        args.add(getSessionAttr("buildingNo"));
        args.add(getSessionAttr("roomNo"));
    }

    private void applyDormFilter(StringBuilder sql, List<Object> args) {
        if (notBlank(getPara("building_no")) || notBlank(getPara("room_no"))) {
            validateDorm(getPara("building_no"), getPara("room_no"));
            sql.append(" and v.building_no = ? and v.room_no = ?");
            args.add(getPara("building_no"));
            args.add(getPara("room_no"));
        }
    }

    private void insertViolation(Map<String, Object> params, AccessService.Scope scope) {
        String collegeId = text(params.get("college_id"));
        String buildingNo = text(params.get("building_no"));
        String roomNo = text(params.get("room_no"));
        String type = text(params.get("violation_type"));
        String description = text(params.get("description"));
        validateDorm(buildingNo, roomNo);
        AccessService.requireCollegeScope(this, collegeId, scope);
        if (Db.findById("college", "college_id", collegeId) == null) {
            throw new BusinessException(400, "教学学院不存在");
        }
        if (Db.findFirst("select 1 from dorm_info where college_id = ? and building_no = ? and room_no = ?",
                collegeId, buildingNo, roomNo) == null) {
            throw new BusinessException(400, "该宿舍不属于所选教学学院");
        }
        if (type.isBlank() || type.length() > 50 || description.isBlank() || description.length() > 1000) {
            throw new BusinessException(400, "请正确填写违规类型和情况说明");
        }
        LocalDateTime occurredAt = parseDateTime(text(params.get("occurred_at")));
        Db.update("insert into violation_info(violation_id, college_id, building_no, room_no, "
                        + "violation_type, description, occurred_at, created_by_type, created_by, "
                        + "created_by_name, created_at) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, now())",
                id(), collegeId, buildingNo, roomNo, type, description, Timestamp.valueOf(occurredAt),
                currentUserType(), currentUserId(), getSessionAttr("name"));
    }

    private LocalDateTime parseDateTime(String value) {
        if (value.isBlank()) {
            return LocalDateTime.now();
        }
        try {
            return LocalDateTime.parse(value, DATE_TIME);
        } catch (DateTimeParseException e) {
            try {
                return LocalDate.parse(value).atTime(LocalTime.NOON);
            } catch (DateTimeParseException ignored) {
                throw new BusinessException(400, "发生时间格式应为 yyyy-MM-dd HH:mm");
            }
        }
    }

    private void requireBoundIfStudent() {
        if ("student".equals(currentUserType())) {
            requireBoundDorm();
        }
    }

    private void validateDorm(String buildingNo, String roomNo) {
        if (buildingNo == null || !buildingNo.matches("\\d{2}")) {
            throw new BusinessException(400, "楼栋号必须为 2 位数字");
        }
        if (roomNo == null || !roomNo.matches("\\d{3}")) {
            throw new BusinessException(400, "房间号必须为 3 位数字");
        }
    }

    private void requireBoundDorm() {
        if (getSessionAttr("buildingNo") == null || getSessionAttr("roomNo") == null) {
            throw new BusinessException(400, "学生账号尚未绑定宿舍");
        }
    }

    private String value(DataFormatter formatter, Cell cell) {
        return cell == null ? "" : formatter.formatCellValue(cell).trim();
    }

    private String id() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    private String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }

    private boolean notBlank(String value) {
        return value != null && !value.isBlank();
    }
}
