package intercept;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import exception.BusinessException;

import java.util.Map;

public class ApiExceptionInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation inv) {
        try {
            inv.invoke();
        } catch (BusinessException e) {
            inv.getController().getResponse().setStatus(e.getCode());
            inv.getController().renderJson(Map.of("code", e.getCode(), "msg", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            inv.getController().getResponse().setStatus(500);
            inv.getController().renderJson(Map.of("code", 500, "msg", "服务器处理请求失败"));
        }
    }
}
