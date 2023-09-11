package Project.OpenBook.Config;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Repository.customer.CustomerRepository;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.codec.cbor.Jackson2CborDecoder;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.servlet.config.annotation.*;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig extends WebMvcConfigurationSupport {

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

    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }



}
