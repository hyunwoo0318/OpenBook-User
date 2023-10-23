package Project.OpenBook.Jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtCustomFilter implements Filter {

    private final TokenManager tokenManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String token = tokenManager.resolveRequest(req);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication authentication = null;
        if(token != null){
            try{
                tokenManager.validateToken(token);
                authentication = tokenManager.getAuthorities(token);
                System.out.println("authentication is ====" + authentication.toString() + " ------------------------------");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch(Exception e){

            }
        }
        chain.doFilter(request, response);
    }

}

