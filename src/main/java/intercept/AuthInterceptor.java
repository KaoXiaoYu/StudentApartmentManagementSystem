package intercept;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import java.util.HashMap;
import java.util.Map;

public class AuthInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();

        // 1. 从 Session 或 Header 中获取用户标识 (这里以 Session 为例)
        Object userId = controller.getSessionAttr("userId");

        // 2. 判断是否已经登录
        if (userId == null) {
            // 3. 未登录，统一返回 JSON 格式的 401 错误提示
            Map<String, Object> result = new HashMap<>();
            result.put("code", 401);
            result.put("msg", "用户未登录或登录已过期");
            controller.renderJson(result);
            return; // 注意：这里直接 return，不再执行后续的 inv.invoke()
        }

        // 4. 已登录，继续执行目标 Action
        inv.invoke();
    }
}