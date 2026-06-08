package servlet;
import java.io.*;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * servlet功能测试
 *
 * @author KaoXiaoYu
 * Time  2026年6月5日
 *
 */
@WebServlet("/servletTest")
public class ServletTest extends HttpServlet {
    private String info="ciallo~";
    @Override
    public void init(ServletConfig config) throws ServletException {
        info="Hello World!";
        System.out.println("Servlet has been init!");
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("received GET request!");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>ServletTest</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>" + info + "</h1>");
        out.println("</body>");
        out.println("</html>");
    }
    @Override
    public void destroy() {

    }
}
