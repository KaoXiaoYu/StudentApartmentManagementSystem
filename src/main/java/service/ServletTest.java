//package service
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

//@WebServlet("/servletTest")
public class ServletTest extends HttpServlet {
    private String info="ciallo~";
    public void init(ServletConfig config) throws ServletException {
        info="Hello World!";
        System.out.println("Servlet has been init!");
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("received GET request");
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<title>ServletTest</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>"+info+"</h1>");
        out.println("</body>");
        out.println("</html>");
    }
    public void destroy() {

    }
}
