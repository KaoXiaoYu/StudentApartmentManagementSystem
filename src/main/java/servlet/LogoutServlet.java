package servlet;
import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;
/**
 * Logout功能
 * @author KaoXiaoYu
 * Time  2026年6月5日
 */
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("/logout Servlet has been init!");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        super.service(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("/logout received GET request!");
        // 强制销毁当前的 Session，清除所有用户状态
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // 清除名为 "remember" 的 Cookie（防止下次自动登录）
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("remember".equals(cookie.getName())) {
                    cookie.setMaxAge(0); // 将存活时间设为0，浏览器会立即删除它
                    cookie.setPath(req.getContextPath() + "/"); // 与创建时一致
                    resp.addCookie(cookie);
                    break; // 找到并处理后即可跳出循环
                }
            }
        }
        // 3. 重定向到登录页面
        resp.sendRedirect(req.getContextPath() + "/login");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("/logout received Post request!");

    }

    @Override
    public void destroy() {

    }
}
