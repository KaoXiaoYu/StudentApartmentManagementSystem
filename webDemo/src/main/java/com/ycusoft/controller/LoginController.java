package com.ycusoft.controller;

import com.ycusoft.entity.dto.LoginDTO;
import com.ycusoft.entity.po.User;
import com.ycusoft.entity.vo.ResultInfo;
import com.ycusoft.service.IUserService;
import com.ycusoft.service.impl.UserServiceImpl;
import com.ycusoft.utils.JSONUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录控制器
 * 处理用户登录请求
 * @author hq
 * @since 2026-05-25
 */
@WebServlet(name = "loginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String LOGIN_USER_SESSION_KEY = "loginUser";

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            LoginDTO loginDTO = JSONUtil.fromJson(request.getInputStream(), LoginDTO.class);

            if (loginDTO.getUsername() == null || loginDTO.getUsername().trim().isEmpty()) {
                out.print(JSONUtil.toJson(ResultInfo.error("用户名不能为空")));
                return;
            }
            if (loginDTO.getPassword() == null || loginDTO.getPassword().trim().isEmpty()) {
                out.print(JSONUtil.toJson(ResultInfo.error("密码不能为空")));
                return;
            }

            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());

            if (user == null) {
                out.print(JSONUtil.toJson(ResultInfo.error("用户名或密码错误")));
                return;
            }

            HttpSession session = request.getSession();
            session.setAttribute(LOGIN_USER_SESSION_KEY, user);

            user.setPassword(null);
            out.print(JSONUtil.toJson(ResultInfo.success("登录成功", user)));
        } catch (Exception e) {
            out.print(JSONUtil.toJson(ResultInfo.error("登录失败：" + e.getMessage())));
        } finally {
            out.flush();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(JSONUtil.toJson(ResultInfo.error("不支持GET请求")));
        out.flush();
    }
}