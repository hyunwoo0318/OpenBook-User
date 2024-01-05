package Project.OpenBook.Jwt;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class JwtCustomFilter implements Filter {

    private final TokenManager tokenManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String token = tokenManager.resolveRequest(req);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Authentication authentication = null;
        if (token != null) {
            try {
                tokenManager.validateToken(token);
                authentication = tokenManager.getAuthorities(token);
                System.out.println("authentication is ====" + authentication.toString()
                    + " ------------------------------");
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {

            }
        }
        chain.doFilter(request, response);
    }

}

