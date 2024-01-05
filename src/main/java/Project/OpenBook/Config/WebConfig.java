package Project.OpenBook.Config;

import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@RequiredArgsConstructor
@EnableWebMvc
public class WebConfig extends WebMvcConfigurationSupport {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Cors관련 설정
     *
     * @param registry
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("**")
            .allowedMethods("OPTIONS", "GET", "POST", "DELETE", "PUT", "PATCH")
            .allowedHeaders("*")
            .allowCredentials(true);
    }

    /**
     * Swagger 리소스 세팅
     */

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
    }


    /**
     * WebClient
     */

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthenticationPrincipalArgumentResolver());
    }


}
