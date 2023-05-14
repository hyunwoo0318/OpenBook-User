package Project.OpenBook.Config;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Repository.AdminRepository;
import Project.OpenBook.Repository.CategoryRepository;
import Project.OpenBook.Repository.ChapterRepository;
import Project.OpenBook.Repository.CustomerRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.SecurityBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.HttpMediaTypeException;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.util.*;

/**
 * Spring Security 관련 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final ChoiceRepository choiceRepository;
    private final TopicRepository topicRepository;

    private final CategoryRepository categoryRepository;

    private final ChapterRepository chapterRepository;

    private final DescriptionRepository descriptionRepository;

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
        return http.csrf().disable().cors().and()
                .formLogin().disable()
                .authorizeRequests()
                .antMatchers("/**").permitAll()
//                .antMatchers(permitAllList).permitAll()
//                .antMatchers("/admin/**").hasRole("ADMIN")
//                .antMatchers("/**").hasRole("USER")
//                .anyRequest().authenticated()
                .and().httpBasic().and().
                headers().frameOptions().sameOrigin().xssProtection().block(false).and().and().build();
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

        if(customerRepository.findByNickName("user1").isEmpty()){
            Customer user1 = Customer.builder()
                    .nickName("user1")
                    .age(20)
                    .currentGrade(5)
                    .role(Role.USER)
                    .build();
            Customer user2 = Customer.builder()
                    .nickName("user2")
                    .age(30)
                    .currentGrade(2)
                    .role(Role.USER)
                    .build();
            Customer user3 = Customer.builder()
                    .nickName("user3")
                    .age(20)
                    .currentGrade(7)
                    .role(Role.USER)
                    .build();
            customerRepository.save(user1);
            customerRepository.save(user2);
            customerRepository.save(user3);
        }

        Category c1 = new Category("유물");
        Category c2 = new Category("사건");
        Category c3 = new Category("국가");
        Category c4 = new Category("인물");

        categoryRepository.saveAllAndFlush(Arrays.asList(c1, c2, c3, c4));

        //단원 전체 저장
        Chapter ch1 = new Chapter("ch1", 1);
        Chapter ch2 = new Chapter("ch2", 2);
        Chapter ch3 = new Chapter("ch3", 3);

        chapterRepository.saveAllAndFlush(Arrays.asList(ch1, ch2, ch3));

        //topic 전체 생성
        Random rand = new Random();
        List<Topic> topicList = new ArrayList<>();


        for (int i = 1; i <= 2000; i++) {
            int year = rand.nextInt(2000) + 1;
            int month = rand.nextInt(12) + 1; // 1~12 사이의 월을 랜덤으로 생성
            int day = rand.nextInt(26) + 1; // 1부터 최대 일수 사이의 일을 랜덤으로 생성
            int length = rand.nextInt(500);

            Integer startDate = year * 1000 + month * 100 + day;
            Integer endDate = startDate + length;

            Category c  = null;

            if(i <= 10){
                c = c1;
            }else if(i <= 20){
                c = c2;
            } else if (i <= 30) {
                c = c3;
            }else {
                c = c4;
            }

            Topic topic = new Topic("topic" + i, startDate, endDate, 0, 0, "detail" + i, ch1, c);
            topicList.add(topic);
        }

        topicRepository.saveAllAndFlush(topicList);

        //선지, 보기 생성
        for (Topic topic : topicList) {
            for (int i = 1; i <= 5; i++) {
                choiceRepository.save(new Choice("choice" + i + " in " + topic.getTitle(), topic));
                descriptionRepository.save(new Description("description" + i + " in " + topic.getTitle(), topic));
            }
        }


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
