package service;

import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import exception.BusinessException;

import javax.servlet.http.Cookie;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

public class AuthService {
    public static final String COOKIE_NAME = "SAMS_REMEMBER";
    private static final int COOKIE_AGE = 7 * 24 * 60 * 60;

    public Record login(Controller controller, Map<String, Object> params) {
        String account = text(params.get("account"));
        String password = text(params.get("password"));
        String accountType = text(params.get("account_type"));

        if (!account.matches("\\d{11}")) {
            throw new BusinessException(400, "账号必须是 11 位学号、工号或手机号");
        }
        if (password.length() < 3 || password.length() > 64) {
            throw new BusinessException(400, "密码长度必须为 3 到 64 位");
        }

        Record user = null;
        if (!"teacher".equals(accountType)) {
            user = Db.findFirst("select student_id user_id, student_id, phone_number, name, role, "
                            + "college_id, building_no, room_no, password_hash, 'student' user_type "
                            + "from student_info where enabled = 1 and (student_id = ? or phone_number = ?)",
                    account, account);
        }
        if (user == null && !"student".equals(accountType)) {
            user = Db.findFirst("select teacher_id user_id, teacher_id, phone_number, name, role, "
                            + "college_id, password_hash, 'teacher' user_type "
                            + "from teacher_info where enabled = 1 and (teacher_id = ? or phone_number = ?)",
                    account, account);
        }
        if (user == null || !PasswordService.matches(password, user.getStr("password_hash"))) {
            throw new BusinessException(401, "账号或密码错误");
        }

        if (!user.getStr("password_hash").startsWith("pbkdf2$")) {
            String table = "student".equals(user.getStr("user_type")) ? "student_info" : "teacher_info";
            String idColumn = "student".equals(user.getStr("user_type")) ? "student_id" : "teacher_id";
            Db.update("update " + table + " set password_hash = ? where " + idColumn + " = ?",
                    PasswordService.hash(password), user.getStr("user_id"));
        }

        putSession(controller, user);
        revoke(controller);
        if (Boolean.TRUE.equals(params.get("remember"))) {
            issueRememberToken(controller, user);
        }
        return publicUser(user);
    }

    public boolean restore(Controller controller) {
        String rawToken = cookieValue(controller, COOKIE_NAME);
        if (rawToken == null || rawToken.isBlank()) {
            return false;
        }
        Record token = Db.findFirst("select * from remember_token where token_hash = ? and expires_at > now()",
                sha256(rawToken));
        if (token == null) {
            clearCookie(controller);
            return false;
        }
        String userType = token.getStr("user_type");
        Record user;
        if ("student".equals(userType)) {
            user = Db.findFirst("select student_id user_id, student_id, phone_number, name, role, college_id, "
                            + "building_no, room_no, 'student' user_type from student_info "
                            + "where enabled = 1 and student_id = ?", token.getStr("user_id"));
        } else {
            user = Db.findFirst("select teacher_id user_id, teacher_id, phone_number, name, role, college_id, "
                            + "'teacher' user_type from teacher_info where enabled = 1 and teacher_id = ?",
                    token.getStr("user_id"));
        }
        if (user == null) {
            revoke(controller);
            return false;
        }
        putSession(controller, user);
        return true;
    }

    public Record currentUser(Controller controller) {
        if (controller.getSessionAttr("userId") == null && !restore(controller)) {
            return null;
        }
        Record result = new Record()
                .set("user_id", controller.getSessionAttr("userId"))
                .set("user_type", controller.getSessionAttr("userType"))
                .set("name", controller.getSessionAttr("name"))
                .set("role", controller.getSessionAttr("role"))
                .set("college_id", controller.getSessionAttr("collegeId"));
        if ("student".equals(controller.getSessionAttr("userType"))) {
            result.set("building_no", controller.getSessionAttr("buildingNo"));
            result.set("room_no", controller.getSessionAttr("roomNo"));
        }
        return result;
    }

    public void logout(Controller controller) {
        revoke(controller);
        if (controller.getRequest().getSession(false) != null) {
            controller.getRequest().getSession(false).invalidate();
        }
    }

    private void issueRememberToken(Controller controller, Record user) {
        String rawToken = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
        String tokenId = UUID.randomUUID().toString().replace("-", "");
        Timestamp expiresAt = Timestamp.from(Instant.now().plus(7, ChronoUnit.DAYS));
        Db.update("delete from remember_token where user_type = ? and user_id = ? and expires_at <= now()",
                user.getStr("user_type"), user.getStr("user_id"));
        Db.update("insert into remember_token(token_id, token_hash, user_type, user_id, expires_at) "
                        + "values (?, ?, ?, ?, ?)",
                tokenId, sha256(rawToken), user.getStr("user_type"), user.getStr("user_id"), expiresAt);

        Cookie cookie = new Cookie(COOKIE_NAME, rawToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_AGE);
        cookie.setSecure(controller.getRequest().isSecure());
        controller.getResponse().addCookie(cookie);
    }

    private void revoke(Controller controller) {
        String rawToken = cookieValue(controller, COOKIE_NAME);
        if (rawToken != null) {
            Db.update("delete from remember_token where token_hash = ?", sha256(rawToken));
        }
        clearCookie(controller);
    }

    private void clearCookie(Controller controller) {
        Cookie cookie = new Cookie(COOKIE_NAME, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        controller.getResponse().addCookie(cookie);
    }

    private void putSession(Controller controller, Record user) {
        controller.setSessionAttr("userId", user.getStr("user_id"));
        controller.setSessionAttr("userType", user.getStr("user_type"));
        controller.setSessionAttr("name", user.getStr("name"));
        controller.setSessionAttr("role", user.getStr("role"));
        controller.setSessionAttr("collegeId", user.getStr("college_id"));
        controller.setSessionAttr("buildingNo", user.getStr("building_no"));
        controller.setSessionAttr("roomNo", user.getStr("room_no"));
    }

    private Record publicUser(Record user) {
        user.remove("password_hash");
        return user;
    }

    private String cookieValue(Controller controller, String name) {
        Cookie[] cookies = controller.getRequest().getCookies();
        if (cookies == null) {
            return null;
        }
        for (Cookie cookie : cookies) {
            if (name.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private String sha256(String value) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String text(Object value) {
        return value == null ? "" : value.toString().trim();
    }
}
