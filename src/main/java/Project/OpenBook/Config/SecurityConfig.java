package Project.OpenBook.Config;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Admin;
import Project.OpenBook.Repository.AdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Optional;

/**
 * Spring Security 관련 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig{

    private final AdminRepository adminRepository;


    private final String[] permitAllList = {
            "/","/admin/login", "/users/login", "/admin/", "/users/",
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
        return http.csrf().disable()
                .formLogin().disable()
                .authorizeRequests()
                .antMatchers(permitAllList).permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/**").hasRole("USER")
                .anyRequest().authenticated()
                .and().httpBasic().and().build();
    }

    @Bean
    public void initAdmin(){
        if(adminRepository.findByLoginId("admin1").isEmpty()) {
            BCryptPasswordEncoder passwordEncoder = passwordEncoder();
            Admin admin = Admin.builder()
                    .loginId("admin1")
                    .password(passwordEncoder.encode("admin1"))
                    .role(Role.ADMIN)
                    .build();
            adminRepository.save(admin);
        }
    }

}
