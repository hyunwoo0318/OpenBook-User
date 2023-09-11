package Project.OpenBook.Config;

import Project.OpenBook.Repository.HttpCookieOAuth2AuthorizationRequestRepository;
import Project.OpenBook.Handler.AuthenticationSuccessCustomHandler;
import Project.OpenBook.Jwt.JwtCustomFilter;
import Project.OpenBook.Service.Customer.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;


/**
 * Spring Security 관련 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtCustomFilter jwtCustomFilter;

    private final String[] permitAllList = {
            "/","/admin/login", "/oauth2/**", "/login/**","/error/*","login/**","/refresh-token",
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/swagger-ui/**",
    };

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf().disable().cors().and()
                .formLogin().disable()
                .authorizeRequests()
//                .antMatchers("**").permitAll()
                .antMatchers(permitAllList).permitAll()
//                .antMatchers("/admin/**").hasAnyRole("ADMIN")
                .antMatchers("/admin/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and().httpBasic().and().
                formLogin().disable().
                exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and().
//                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and().
                headers()
                .frameOptions().sameOrigin().xssProtection().block(false).and().and()
               .addFilterBefore(jwtCustomFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver(){
        return new AuthenticationPrincipalArgumentResolver();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList(HttpMethod.GET.name(),HttpMethod.PATCH.name(),HttpMethod.OPTIONS.name(), HttpMethod.POST.name(),HttpMethod.DELETE.name()));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
