package servlet;

import java.io.*;
import java.sql.*;
import com.jfinal.kit.PropKit;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Cookie;



/**
 * Login功能测试
 * @author KaoXiaoYu
 * Time  2026年6月5日
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    //Class.forName("com.mysql.cj.jdbc.Driver");

    String url=PropKit.use("database.properties").get("student_info_url_mysql");
    String dbusr=PropKit.use("database.properties").get("student_info_usr_mysql");
    String dbpswd=PropKit.use("database.properties").get("student_info_pswd_mysql");
    String driver=PropKit.use("database.properties").get("student_info_driver_mysql");


    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("/login Servlet has been init!");
        System.out.println(url);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("/login received GET request!");
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("/login received Post request!");

        //已登录则跳转
        IsLogged isLogged = new IsLogged();
        if(isLogged.check(req,resp)){
            resp.sendRedirect(req.getContextPath() + "/HOME");
            return;
        }
        req.setCharacterEncoding("UTF-8");
        // 获取前端传来的账号和密码，此处暂时无功能，作为演示使用
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        String sql,info;//输入的是手机号/学生号
        if(username.length()==11){
            sql = "select * from student_info where phone_number=? ";
            info="phone_number";
        }else{
            sql = "select * from student_info where student_id=? ";
            info="student_id";
        }

        String pswd=null;
        //连接数据库
        try (Connection conn = DriverManager.getConnection(url, dbusr, dbpswd);
             PreparedStatement pstmt = conn.prepareStatement(sql);) {
            pstmt.setInt(1, 20);
            // 执行查询并获取结果集
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    pswd= rs.getString("password");
                    System.out.println(pswd);
                    rs.close();
                }
            }
        } catch (SQLException e) {
            System.out.println("数据库链接失败");
            e.fillInStackTrace();
        }


        //执行账号密码校验功能
        boolean isAuthenticated = false;
        if (pswd != null) {
            isAuthenticated = pswd.equals(password);
        }


        if (isAuthenticated) {
            // 登录成功
            // 将用户名保存到 Session 中
            HttpSession session = req.getSession();
            session.setAttribute("username", username);

            //  向浏览器发送cookie
            String remember = req.getParameter("remember");
            if ("1".equals(remember)) {

                // 创建 Cookie，保存用户名
                Cookie cookie = new Cookie("remember", username);
                // 设置 Cookie 存活时间为 7 天 (单位：秒)
                cookie.setMaxAge(60 * 60 * 24 * 7);
                // 设置路径为根目录，防止不同路径下无法读取 Cookie
                cookie.setPath("/");
                // 将 Cookie 发送给浏览器
                resp.addCookie(cookie);
            }

            // 重定向到系统主页
            resp.sendRedirect(req.getContextPath() + "/HOME");


        } else {
            // 登录失败
            // 将错误信息放入 request，并转发回登录页显示
            req.setAttribute("login_msg", "账号或密码错误！");
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }

    @Override
    public void destroy() {

    }
}
