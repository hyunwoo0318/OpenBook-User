package Project.OpenBook.Config;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Repository.HttpCookieOAuth2AuthorizationRequestRepository;
import Project.OpenBook.Handler.AuthenticationSuccessCustomHandler;
import Project.OpenBook.Jwt.JwtCustomFilter;
import Project.OpenBook.Repository.admin.AdminRepository;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.*;

import static java.lang.Long.parseLong;

/**
 * Spring Security 관련 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AdminRepository adminRepository;

    private final OAuthService oAuthService;

    private final AuthenticationSuccessCustomHandler authenticationSuccessCustomHandler;

    private final JwtCustomFilter jwtCustomFilter;

    private final String[] permitAllList = {
            "/","/admin/login", "/users/login", "/admin/", "/users/","/oauth2/**", "/login/**","/error/*",
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
                .antMatchers("/**").permitAll()
//                .antMatchers(permitAllList).permitAll()
//                .antMatchers("/admin/**").hasAnyRole("ADMIN", "USER")
//                .antMatchers("/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
                .and().httpBasic().and().
                formLogin().disable().
                sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER).and().
                oauth2Login()
                    .successHandler(authenticationSuccessCustomHandler)
                .redirectionEndpoint().and()
                .userInfoEndpoint().userService(oAuthService)
                .and().authorizationEndpoint()
                    .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and().and()
                .headers()
                .frameOptions().sameOrigin().xssProtection().block(false).and().and()
               .addFilterBefore(jwtCustomFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }






    @Bean
    public void initAdmin(){
        if(adminRepository.findByLoginId("admin1").isEmpty()) {
            BCryptPasswordEncoder passwordEncoder = passwordEncoder();
            Admin admin1 = Admin.builder()
                    .loginId("admin1")
                    .password(passwordEncoder.encode("admin1"))
                    .role(Role.ADMIN)
                    .build();
            Admin admin2 = Admin.builder()
                    .loginId("admin2")
                    .password(passwordEncoder.encode("admin2"))
                    .role(Role.ADMIN)
                    .build();
            adminRepository.save(admin1);
            adminRepository.save(admin2);
        }



    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
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
