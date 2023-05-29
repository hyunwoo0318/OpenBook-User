package Project.OpenBook.Service;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Jwt.TokenDto;
import Project.OpenBook.Repository.customer.CustomerRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.endpoint.OAuth2AccessTokenResponse;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final CustomerRepository customerRepository;

    private final String REDIRECT_URL_LOGIN = "http://localhost:8080/login/oauth2/code/kakao";
    private final String REQ_URL_TOKEN = "https://kauth.kakao.com/oauth/token";
    private final String REQ_URL_INFO = "https://kapi.kakao.com/v2/user/me";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private final String key;


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

        Customer customer = customerRepository.queryCustomer(oAuthId, provider);
        if (customer == null) {
            //회원가입이 되어있지 않음 -> 회원가입 시킴
            customer = Customer.builder()
                    .oAuthId(oAuthId)
                    .provider(provider)
                    .role(Role.USER)
                    .nickName(UUID.randomUUID().toString())
                    .build();
            customerRepository.save(customer);
        }

        return customer;
    }

    public TokenDto login(String providerName, String code) {
        String token= "";
        String oauthId = "";
        if (providerName.equals("kakao")) {
            String kakaoToken = getKakaoToken(code);
            oauthId = String.valueOf(getKakaoId(kakaoToken));
        }

        Customer customer = customerRepository.queryCustomer(oauthId, providerName);
        if (customer == null) {
            //회원가입이 되어있지 않음 -> 회원가입 시킴
            customer = Customer.builder()
                    .oAuthId(oauthId)
                    .provider(providerName)
                    .role(Role.USER)
                    .nickName(UUID.randomUUID().toString())
                    .build();
            customerRepository.save(customer);
        }

        return null;


    }

    public String getKakaoToken(String code){

        String accessToken = "";
        String refreshToken = "";
        String redirectURL = "";

        {
            redirectURL = REDIRECT_URL_LOGIN;
        }

        try{
            URL url = new URL(REQ_URL_TOKEN);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            //OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            connection.setDoOutput(true);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("grant_type=authorization_code");
            stringBuilder.append("&client_id=" + key);
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

    public Long getKakaoId(String token) {
        Long id=-1L;
        try{
            URL url = new URL(REQ_URL_INFO);
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




    private OAuth2Token getConnect(String code, ClientRegistration provider) {
        return WebClient.create()
                .post()
                .uri(provider.getProviderDetails().getTokenUri())
                .headers(h -> {
                    h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                    h.setAcceptCharset(Collections.singletonList(StandardCharsets.UTF_8));
                })
                .bodyValue(tokenRequest(code, provider))
                .retrieve()
                .bodyToMono(OAuth2Token.class)
                .block();
    }

    private MultiValueMap<String, String> tokenRequest(String code, ClientRegistration provider) {
        LinkedMultiValueMap map = new LinkedMultiValueMap();
        map.add("code", code);
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", provider.getRedirectUri());
        map.add("client_secret", provider.getClientSecret());
        map.add("client_id", provider.getClientId());
        return map;
    }
}
