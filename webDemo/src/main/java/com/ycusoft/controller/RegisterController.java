package com.ycusoft.controller;

import com.ycusoft.entity.dto.RegisterDTO;
import com.ycusoft.entity.vo.ResultInfo;
import com.ycusoft.service.IUserService;
import com.ycusoft.service.impl.UserServiceImpl;
import com.ycusoft.utils.JSONUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 注册控制器
 * 处理用户注册请求
 *
 * @author hq
 * @since 2026-05-25
 */
@WebServlet(name = "registerController", urlPatterns = {"/register"})
public class RegisterController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final IUserService userService = new UserServiceImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            // 解析请求体
            RegisterDTO registerDTO = JSONUtil.fromJson(request.getInputStream(), RegisterDTO.class);

            // 参数校验
            if (registerDTO.getUsername() == null || registerDTO.getUsername().trim().isEmpty()) {
                out.print(JSONUtil.toJson(ResultInfo.error("用户名不能为空")));
                return;
            }

            if (registerDTO.getPassword() == null || registerDTO.getPassword().trim().isEmpty()) {
                out.print(JSONUtil.toJson(ResultInfo.error("密码不能为空")));
                return;
            }

            if (registerDTO.getConfirmPassword() == null || registerDTO.getConfirmPassword().trim().isEmpty()) {
                out.print(JSONUtil.toJson(ResultInfo.error("确认密码不能为空")));
                return;
            }

            // 验证密码是否一致
            if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
                out.print(JSONUtil.toJson(ResultInfo.error("两次输入的密码不一致")));
                return;
            }

            // 用户名长度校验
            if (registerDTO.getUsername().length() < 4 || registerDTO.getUsername().length() > 20) {
                out.print(JSONUtil.toJson(ResultInfo.error("用户名长度必须在4-20个字符之间")));
                return;
            }

            // 密码长度校验
            if (registerDTO.getPassword().length() < 6 || registerDTO.getPassword().length() > 32) {
                out.print(JSONUtil.toJson(ResultInfo.error("密码长度必须在6-32个字符之间")));
                return;
            }

            // 调用注册服务
            boolean success = userService.register(registerDTO.getUsername(), registerDTO.getPassword(), registerDTO.getUsername());

            if (success) {
                out.print(JSONUtil.toJson(ResultInfo.success("注册成功")));
            } else {
                out.print(JSONUtil.toJson(ResultInfo.error("注册失败，用户名已存在")));
            }
        } catch (Exception e) {
            out.print(JSONUtil.toJson(ResultInfo.error("注册失败：" + e.getMessage())));
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
