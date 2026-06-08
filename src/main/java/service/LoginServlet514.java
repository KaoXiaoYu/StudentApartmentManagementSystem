package service;
import Controller.IsLogged;

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
 * Login功能测试
 * @author KaoXiaoYu
 * Time  2026年6月5日
 */
@WebServlet("/login514")
public class LoginServlet514 extends HttpServlet {
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


        String sql = "select * from student_info where student_id=? ";
        req.setCharacterEncoding("UTF-8");
        // 获取前端传来的账号和密码，此处暂时无功能，作为演示使用
        String username = req.getParameter("username");
        String password = req.getParameter("password");


        //连接数据库




        //执行账号密码校验功能
        boolean isAuthenticated = false ;
        if("admin".equals(username)&&"123456".equals(password)){
            isAuthenticated = true;
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