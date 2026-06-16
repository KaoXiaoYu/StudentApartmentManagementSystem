package intercept;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import exception.BusinessException;

import java.util.Map;

/**
 * API全局异常拦截器
 * 统一捕获接口业务异常与系统未知异常，标准化返回JSON错误信息
 * 作用于Controller接口请求，对所有被拦截的接口做异常统一处理
 */
public class ApiExceptionInterceptor implements Interceptor {

    /**
     * 拦截核心执行方法
     * @param inv JFinal调用对象，持有控制器、请求、响应及目标方法信息
     */
    @Override
    public void intercept(Invocation inv) {
        try {
            // 执行目标Controller接口方法
            inv.invoke();
        } catch (BusinessException e) {
            // 捕获自定义业务异常：返回业务指定状态码与异常提示信息
            inv.getController().getResponse().setStatus(e.getCode());
            // 输出标准JSON错误格式：code状态码、msg异常描述
            inv.getController().renderJson(Map.of("code", e.getCode(), "msg", e.getMessage()));
        } catch (Exception e) {
            // 捕获所有其余未知系统异常
            e.printStackTrace();
            // 设置HTTP 500服务器错误状态码
            inv.getController().getResponse().setStatus(500);
            // 统一返回系统错误提示，屏蔽底层详细异常信息避免泄露
            inv.getController().renderJson(Map.of("code", 500, "msg", "服务器处理请求失败"));
        }
    }
}