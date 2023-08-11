package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Constants.KakaoConst;
import Project.OpenBook.Constants.NaverConst;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.ChapterProgress;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Domain.TopicProgress;
import Project.OpenBook.Jwt.TokenDto;
import Project.OpenBook.Jwt.TokenManager;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.chapter.ChapterRepositoryCustom;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final CustomerRepository customerRepository;
    private final TokenManager tokenManager;
    private final ChapterProgressRepository chapterProgressRepository;
    private final TopicProgressRepository topicProgressRepository;
    private final TopicRepository topicRepository;
    private final ChapterRepository chapterRepository;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String kakaoKey;
    @Override
    public Customer loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String oAuthId = "";
        if (provider.equals("kakao")) {
            oAuthId = oAuth2User.getName();
        } else if (provider.equals("naver")) {
            Map<String, Object> attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            oAuthId = (String) attributes.get("id");
        }


        Optional<Customer> customerOptional = customerRepository.queryCustomer(oAuthId, provider);
        if (customerOptional.isEmpty()) {
            Customer customer = Customer.builder()
                    .oAuthId(oAuthId)
                    .provider(provider)
                    .role(Role.USER)
                    .nickName(UUID.randomUUID().toString())
                    .build();
            customerRepository.save(customer);
            return customer;
        }else{
            return customerOptional.get();
        }

    }

    public TokenDto login(String providerName, String code) {
        String token= "";
        String oauthId = "";
        if (providerName.equals("kakao")) {
            String kakaoToken = getKakaoToken(code);
            oauthId = String.valueOf(getKakaoId(kakaoToken));
        } else if (providerName.equals("naver")) {
            String naverToken = getNaverToken(code);
            oauthId = getNaverId(naverToken);
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

    public String getKakaoToken(String code){

        String accessToken = "";
        String refreshToken = "";
        String redirectURL = "";



        redirectURL = KakaoConst.REDIRECT_URL_LOGIN;

        try{
            URL url = new URL(KakaoConst.REQ_URL_TOKEN);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            connection.setDoOutput(true);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("grant_type=authorization_code");
            stringBuilder.append("&client_id=" + kakaoKey);
            stringBuilder.append("&redirect_uri=" + redirectURL);
            stringBuilder.append("&code=" + code);
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.flush();

            int httpCode = connection.getResponseCode();
            if(httpCode == 200){
                System.out.println("success!");
            }else{
                throw new RuntimeException("responseCode Error!" + httpCode);
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String body = "";

            while ((line = bufferedReader.readLine()) != null) {
                body += line;
            }
            System.out.println(body);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            accessToken = jsonNode.get("access_token").toString();
            refreshToken = jsonNode.get("refresh_token").toString();

            bufferedReader.close();
            bufferedWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return accessToken;
    }

    public String getNaverToken(String code){

        String accessToken = "";
        String refreshToken = "";
        String redirectURL = "";


        redirectURL = NaverConst.REDIRECT_URL_LOGIN;


        try{
            URL url = new URL(NaverConst.REQ_URL_TOKEN);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            connection.setDoOutput(true);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("grant_type=authorization_code");
            stringBuilder.append("&client_id=" + NaverConst.CLIENT_ID);
            stringBuilder.append("&client_secret" + NaverConst.CLIENT_SECRET);
            stringBuilder.append("&redirect_uri=" + redirectURL);
            stringBuilder.append("&code=" + code);
            bufferedWriter.write(stringBuilder.toString());
            bufferedWriter.flush();

            int httpCode = connection.getResponseCode();
            if(httpCode == 200){
                System.out.println("success!");
            }else{
                throw new RuntimeException("responseCode Error!" + httpCode);
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String body = "";

            while ((line = bufferedReader.readLine()) != null) {
                body += line;
            }
            System.out.println(body);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            accessToken = jsonNode.get("access_token").toString();
            refreshToken = jsonNode.get("refresh_token").toString();

            bufferedReader.close();
            bufferedWriter.close();

        }catch (Exception e){
            e.printStackTrace();
        }
        return accessToken;
    }

    public String getNaverId(String token) {
        String id="";
        try{
            URL url = new URL(NaverConst.REQ_URL_INFO);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("responseCode Error!" + responseCode);
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String body = "";

            while ((line = bufferedReader.readLine()) != null) {
                body += line;
            }

            System.out.println(body);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            id = jsonNode.get("id").toString();

            bufferedReader.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }

    public Long getKakaoId(String token) {
        Long id=-1L;
        try{
            URL url = new URL(KakaoConst.REQ_URL_INFO);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            connection.setRequestProperty("Authorization", "Bearer " + token);

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("responseCode Error!" + responseCode);
            }

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = "";
            String body = "";

            while ((line = bufferedReader.readLine()) != null) {
                body += line;
            }

            System.out.println(body);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(body);

            id = jsonNode.get("id").asLong();

            bufferedReader.close();

        } catch (Exception e){
            e.printStackTrace();
        }
        return id;
    }


}
