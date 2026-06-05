package service;
import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Login功能测试
 * @author KaoXiaoYu
 * Time  2026年6月5日
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("/login Servlet has been init!");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        super.service(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("/login received GET request!");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("/login received Post request!");
        req.setCharacterEncoding("UTF-8");

        // 2. 获取前端传来的账号和密码
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        // 3. 核心验证逻辑（这里以简单的硬编码为例，实际应查库）
        boolean isAuthenticated = "admin".equals(username) && "123456".equals(password);

        if (isAuthenticated) {
            // 4. 登录成功：将用户名保存到 Session 中
            HttpSession session = req.getSession();
            session.setAttribute("user", username);

            // 5. 重定向到系统主页（防止表单重复提交）
            resp.sendRedirect("home.jsp");
        } else {
            // 6. 登录失败：将错误信息放入 request，并转发回登录页显示
            req.setAttribute("error", "账号或密码错误！");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    @Override
    public void destroy() {

    }
}
