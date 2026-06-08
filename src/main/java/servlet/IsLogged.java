package servlet;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class IsLogged {
    public boolean check(HttpServletRequest req, HttpServletResponse resp)throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        //检查session
        if (session != null) {
            Object username = session.getAttribute("username");
            if (username != null) {
                return true;
            }
        }
        //session不存在则检测
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // 查找名为 "remember" 的 Cookie
                if ("remember".equals(cookie.getName())) {
                    String username = cookie.getValue();
                    // 确认有效后，重新将用户信息存入 Session
                    HttpSession newSession = req.getSession(true); // 此时才允许创建新 Session
                    newSession.setAttribute("username", username);
                    return true;
                }
            }
        }
        return false;
    }
}
