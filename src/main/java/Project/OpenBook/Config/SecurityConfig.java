package Project.OpenBook.Config;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.*;
import Project.OpenBook.HttpCookieOAuth2AuthorizationRequestRepository;
import Project.OpenBook.AuthenticationSuccessCustomHandler;
import Project.OpenBook.Jwt.JwtCustomFilter;
import Project.OpenBook.Repository.AdminRepository;
import Project.OpenBook.Repository.CategoryRepository;
import Project.OpenBook.Repository.ChapterRepository;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;

    private final OAuthService oAuthService;

    private final AuthenticationSuccessCustomHandler authenticationSuccessCustomHandler;

    private final JwtCustomFilter jwtCustomFilter;
    private final ChoiceRepository choiceRepository;
    private final TopicRepository topicRepository;

    private final CategoryRepository categoryRepository;

    private final ChapterRepository chapterRepository;

    private final DescriptionRepository descriptionRepository;

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


        //카테고리 전체 저장
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


        for (int i = 1; i <= 1000; i++) {
            int year = rand.nextInt(2000) + 1;
            int month = rand.nextInt(12) + 1; // 1~12 사이의 월을 랜덤으로 생성
            int day = rand.nextInt(26) + 1; // 1부터 최대 일수 사이의 일을 랜덤으로 생성
            int length = rand.nextInt(500);

            Integer startDate = year * 1000 + month * 100 + day;
            Integer endDate = (year + length) * 1000 + month * 100 + day;

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
