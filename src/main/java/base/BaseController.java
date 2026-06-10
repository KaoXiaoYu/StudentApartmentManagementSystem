package base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jfinal.core.Controller;
import exception.BusinessException;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseController extends Controller {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    protected Map<String, Object> body() {
        try {
            String raw = getRawData();
            if (raw == null || raw.isBlank()) {
                return new LinkedHashMap<>();
            }
            return MAPPER.readValue(raw, Map.class);
        } catch (Exception e) {
            throw new BusinessException(400, "请求 JSON 格式错误");
        }
    }

    protected void ok(String message) {
        ok(message, null);
    }

    protected void ok(String message, Object data) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("code", 200);
        result.put("msg", message);
        if (data != null) {
            result.put("data", data);
        }
        renderJson(result);
    }

    protected void fail(BusinessException e) {
        getResponse().setStatus(e.getCode() >= 400 && e.getCode() < 600 ? e.getCode() : 400);
        renderJson(Map.of("code", e.getCode(), "msg", e.getMessage()));
    }

    protected String currentUserType() {
        return getSessionAttr("userType");
    }

    protected String currentUserId() {
        return getSessionAttr("userId");
    }

    protected String currentRole() {
        return getSessionAttr("role");
    }

    protected String currentCollegeId() {
        return getSessionAttr("collegeId");
    }
}
