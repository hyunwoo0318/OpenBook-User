package Project.OpenBook;

import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Jwt.TokenDto;
import Project.OpenBook.Jwt.TokenManager;
import Project.OpenBook.Repository.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Component
@RequiredArgsConstructor
public class AuthenticationSuccessCustomHandler implements AuthenticationSuccessHandler {

    private final TokenManager tokenManager;
    private final CustomerRepository customerRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        AuthenticationSuccessHandler.super.onAuthenticationSuccess(request, response, chain, authentication);
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Customer customer = (Customer)authentication.getPrincipal();
        TokenDto tokenDto = tokenManager.generateToken(authentication, customer.getId());
        response.setHeader("Authorization", tokenDto.getType() + " " + tokenDto.getAccessToken());
        response.setHeader("Refresh-token", tokenDto.getRefreshToken());
        if (customer.isNew()) {
            response.setHeader("Is-new", "T");
        }
        response.setStatus(201);
    }
}
