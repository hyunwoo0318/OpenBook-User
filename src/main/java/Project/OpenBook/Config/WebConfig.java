package Project.OpenBook.Config;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Repository.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cors관련 설정
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("**")
                .allowedMethods("OPTIONS", "GET", "POST", "DELETE", "PUT", "PATCH")
                .allowedHeaders("*");
    }

    /**
     * Swagger 리소스 세팅
     */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }

    /**
     * 기본 관리자 아이디 세팅
     */

    @Bean
    public void initAdmin(){
        if(customerRepository.findByNickName("admin1").isEmpty()){
            Customer admin1 = new Customer("admin1", passwordEncoder.encode("admin1"), Role.ADMIN);
            Customer admin2 = new Customer("admin2", passwordEncoder.encode("admin2"), Role.ADMIN);
            customerRepository.saveAll(Arrays.asList(admin1, admin2));
        }
    }

    /**
     * WebClient
     */

    public WebClient.Builder getWebClientBuilder(){
        return WebClient.builder();
    }


}
