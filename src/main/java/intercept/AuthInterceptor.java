package intercept;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import service.AuthService;

import java.util.Map;

/**
 * 登录认证拦截器
 * 校验用户登录状态，未登录/会话失效直接返回401未授权JSON提示
 * 所有需要登录权限的接口统一配置该拦截器
 */
public class AuthInterceptor implements Interceptor {

    // 认证业务服务对象
    private final AuthService authService = new AuthService();

    /**
     * 拦截校验逻辑
     * @param inv JFinal执行上下文，包含控制器、请求方法等信息
     */
    @Override
    public void intercept(Invocation inv) {
        // 获取当前请求控制器
        Controller controller = inv.getController();

        // 判断session无用户ID，且自动恢复登录失败 → 判定未登录
        if (controller.getSessionAttr("userId") == null && !authService.restore(controller)) {
            // 设置HTTP 401未登录状态码
            controller.getResponse().setStatus(401);
            // 返回统一JSON错误信息
            controller.renderJson(Map.of("code", 401, "msg", "请先登录"));
            // 终止后续接口方法执行
            return;
        }

        // 登录校验通过，执行目标接口方法
        inv.invoke();
    }
}