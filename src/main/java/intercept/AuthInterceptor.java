package intercept;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import service.AuthService;

import java.util.Map;

public class AuthInterceptor implements Interceptor {
    private final AuthService authService = new AuthService();

    @Override
    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        if (controller.getSessionAttr("userId") == null && !authService.restore(controller)) {
            controller.getResponse().setStatus(401);
            controller.renderJson(Map.of("code", 401, "msg", "请先登录"));
            return;
        }
        inv.invoke();
    }
}
