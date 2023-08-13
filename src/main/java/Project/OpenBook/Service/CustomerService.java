package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Constants.KakaoConst;
import Project.OpenBook.Constants.NaverConst;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.ChapterProgress;
import Project.OpenBook.Domain.TopicProgress;
import Project.OpenBook.Dto.customer.CustomerCodeList;
import Project.OpenBook.Dto.customer.CustomerDetailDto;
import Project.OpenBook.Jwt.TokenDto;
import Project.OpenBook.Jwt.TokenManager;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Dto.customer.CustomerAddDetailDto;
import Project.OpenBook.Repository.customer.CustomerRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;
    private final TopicProgressRepository topicProgressRepository;
    private final ChapterProgressRepository chapterProgressRepository;

    private final AuthenticationManagerBuilder authenticationManager;
    private final WebClient.Builder webClientBuilder;

    private final TokenManager tokenManager;

    public Customer addDetails(Long customerId, CustomerAddDetailDto customerAddDetailDto) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomException(CUSTOMER_NOT_FOUND));
        String nickname = customerAddDetailDto.getNickname();
        checkDupNickname(nickname);

        customer.addDetails(nickname, customerAddDetailDto.getAge(), customerAddDetailDto.getExpertise());
        return customer;
    }

    private void checkDupNickname(String nickname) {
        customerRepository.findByNickName(nickname).ifPresent(c ->{
           throw new CustomException(DUP_NICKNAME);
        });
    }

    public CustomerCodeList queryCustomers() {
        List<String> codeList = customerRepository.findAll().stream().map(c -> c.getCode()).collect(Collectors.toList());
        return new CustomerCodeList(codeList);
    }

    public CustomerDetailDto queryCustomerDetail(String code) {
        Customer customer = customerRepository.queryCustomer(code)
                .orElseThrow(() -> {
                    throw new CustomException(CUSTOMER_NOT_FOUND);
                });
        return CustomerDetailDto.builder()
                .age(customer.getAge())
                .log(null)
                .expertise(customer.getExpertise())
                .isSubscribed(customer.isSubscribed())
                .nickName(customer.getNickName())
                .build();
    }

    public void deleteCustomer(long customerId) {
        customerRepository.deleteById(customerId);
    }

    public Optional<Customer> queryCustomer(String nickName){
        return customerRepository.findByNickName(nickName);
    }

    /**
     * 관리자
     */

    public void loginAdmin(String loginId, String password){
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(loginId, password);
        Authentication authentication = authenticationManager.getObject().authenticate(upToken);

        if(!authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }

    @Override
    public Customer loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByNickName(username).orElseThrow(() -> {
            throw new CustomException(LOGIN_FAIL);
        });
    }


    /**
     * Oauth2
     */
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoKey;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    public TokenDto loginOauth2(String providerName, String code) throws Exception{
        String oauthId = "";
        if (providerName.equals("kakao")) {
            oauthId = kakaoApi(code);
        } else if (providerName.equals("naver")) {
            oauthId = naverApi(code);
        }

        Customer customer;
        Optional<Customer> customerOptional = customerRepository.queryCustomer(oauthId, providerName);
        if (customerOptional.isEmpty()) {
            //회원가입이 되어있지 않음 -> 회원가입 시킴
            customer = Customer.builder()
                    .oAuthId(oauthId)
                    .provider(providerName)
                    .role(Role.USER)
                    .nickName(UUID.randomUUID().toString())
                    .build();
            customerRepository.save(customer);

            //단원학습, 주제학습 레코드 생성
            updateChapterProgress(customer);
            updateTopicProgress(customer);
        }else{
            customer = customerOptional.get();
        }

        String authorities = customer.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        TokenDto tokenDto = tokenManager.generateToken(authorities, customer.getId());
        return tokenDto;
    }

    private void updateTopicProgress(Customer customer) {
        List<TopicProgress> topicProgressList = topicRepository.findAll().stream()
                .map(t -> new TopicProgress(customer, t))
                .collect(Collectors.toList());
        topicProgressRepository.saveAll(topicProgressList);
    }

    private void updateChapterProgress(Customer customer) {
        List<ChapterProgress> chapterProgressList = chapterRepository.findAll().stream()
                .map(c -> new ChapterProgress(customer, c))
                .collect(Collectors.toList());
        chapterProgressRepository.saveAll(chapterProgressList);
    }

    public String kakaoApi(String code) throws Exception{

        String redirectURL = KakaoConst.REDIRECT_URL_LOGIN;

        Map<String, String> map = webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("kauth.kakao.com")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoKey)
                        .queryParam("redirect_uri", redirectURL)
                        .queryParam("code", code)
                        .build()
                )
                .header("Content-Type", "application/x-www-form-urlencoded;charset=utf-8")
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> {
                    throw new CustomException(LOGIN_FAIL);
                })
                .block();


        String idToken = map.get("id_token");
        String kakaoId = getKakaoId(idToken);

        return kakaoId;
    }

    public String naverApi(String code) throws JsonProcessingException, UnsupportedEncodingException {

        String state = URLEncoder.encode("https://nid.naver.com/oauth2.0/token", StandardCharsets.UTF_8.toString());

        Map<String, String> map = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("nid.naver.com")
                        .path("/oauth2.0/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", naverClientId)
                        .queryParam("client_secret", naverClientSecret)
                        .queryParam("code", code)
                        .queryParam("state", state)
                        .build())
                .retrieve()
                .bodyToMono(Map.class)
                .onErrorResume(e -> {
                    throw new CustomException(LOGIN_FAIL);
                })
                .block();

        //오류가 있는 상황
        if(map.containsKey("error")){
            throw new CustomException(LOGIN_FAIL);
        }

        String accessToken = map.get("access_token");
        String naverId = getNaverId(accessToken);
        return naverId;
    }

    private String getNaverId(String accessToken) throws JsonProcessingException {
        String body = webClientBuilder.build()
                .get()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("openapi.naver.com")
                        .path("/v1/nid/me")
                        .build())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(e -> {
                    throw new CustomException(LOGIN_FAIL);
                })
                .block();

        ObjectMapper objectMapper = new ObjectMapper();
        String message = objectMapper.readTree("message").toString();
        if(!message.equals("success")){
            throw new CustomException(LOGIN_FAIL);
        }
        String naverId = objectMapper.readTree(body).get("response").get("id").toString();

        return naverId;

    }


    public String getKakaoId(String idToken) throws JsonProcessingException {
        String[] split = idToken.split("[.]");
        String header = split[0];
        String body = split[1];
        String sign = split[2];

        //TODO : sign 검증

        //body검증
        String decodedBody = new String(Base64Utils.decode(body.getBytes()));

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> bodyMap = objectMapper.readValue(decodedBody, Map.class);

        return bodyMap.get("sub");
    }

}
