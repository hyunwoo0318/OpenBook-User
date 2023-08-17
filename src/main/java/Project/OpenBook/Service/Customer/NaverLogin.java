package Project.OpenBook.Service.Customer;

import Project.OpenBook.Utils.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import static Project.OpenBook.Constants.ErrorCode.LOGIN_FAIL;


@Component
public class NaverLogin implements Oauth2Login {

    private WebClient.Builder webClientBuilder;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    public NaverLogin(@Value("${spring.security.oauth2.client.registration.naver.client-id}") String naverClientId, @Value("${spring.security.oauth2.client.registration.naver.client-secret}") String naverClientSecret, WebClient.Builder builder) {
        this.naverClientId = naverClientId;
        this.naverClientSecret = naverClientSecret;
        this.webClientBuilder = builder;
    }
    @Override
    public String login(String code) throws UnsupportedEncodingException, JsonProcessingException {
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
}
