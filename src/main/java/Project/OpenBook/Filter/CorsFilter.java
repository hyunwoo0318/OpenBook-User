package Project.OpenBook.Filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component
//public class CorsFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
//        HttpServletResponse response = (HttpServletResponse) res;
//        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
//        response.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET, POST, PUT, DELETE");
//        response.setHeader("Access-Control-Allow-Credentials", "true");
//        chain.doFilter(req, res);
//    }
//}