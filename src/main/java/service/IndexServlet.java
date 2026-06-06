package service;

import Controller.IsLogged;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import javax.servlet.http.Cookie;

/**
 * 默认页功能测试
 * @author KaoXiaoYu
 * Time  2026年6月6日
 */
@WebServlet("/")
public class IndexServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        System.out.println("/ Servlet has been init!");
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.service(req, resp);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("/ received GET request!");
        IsLogged isLogged = new IsLogged();
        if(isLogged.check(req,resp)){
            resp.sendRedirect(req.getContextPath() + "/HOME");
        }else{
            resp.sendRedirect(req.getContextPath() + "/login");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("/ received Post request!");
    }

    @Override
    public void destroy() {

    }
}
