package com.qcecuring.config;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

@Component
public class BasicAuthFilter implements Filter {
  private final String USER = "admin";
  private final String PASS = "password123";

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;
    String path = request.getRequestURI();
    if (path.startsWith("/api/")){
      String auth = request.getHeader("Authorization");
      if (auth == null || !auth.startsWith("Basic ")) {
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Unauthorized access. Please provide valid credentials.\"}");
        return;
      }
      try {
        String base = auth.substring(6);
        String cred = new String(Base64.getDecoder().decode(base), StandardCharsets.UTF_8);
        String[] parts = cred.split(":",2);
        if (parts.length!=2 || !USER.equals(parts[0]) || !PASS.equals(parts[1])){
          response.setStatus(401);
          response.setContentType("application/json");
          response.getWriter().write("{\"error\":\"Unauthorized access. Please provide valid credentials.\"}");
          return;
        }
      } catch (IllegalArgumentException e){
        response.setStatus(401);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Unauthorized access. Please provide valid credentials.\"}");
        return;
      }
    }
    chain.doFilter(req,res);
  }
}
