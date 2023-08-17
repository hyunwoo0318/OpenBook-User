package Project.OpenBook.Service.Customer;

import Project.OpenBook.Constants.KakaoConst;
import Project.OpenBook.Utils.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;

import javax.annotation.PostConstruct;
import java.util.Map;

import static Project.OpenBook.Constants.ErrorCode.LOGIN_FAIL;

@Component
public class KakaoLogin implements Oauth2Login{
    private final String redirectURL = KakaoConst.REDIRECT_URL_LOGIN;
    private WebClient.Builder webClientBuilder;
    private String kakaoKey;

    public KakaoLogin(@Value("${spring.security.oauth2.client.registration.kakao.client-id}") String kakaoKey, WebClient.Builder builder) {
        this.kakaoKey = kakaoKey;
        this.webClientBuilder = builder;
    }

    @Override
    public String login(String code) throws JsonProcessingException {
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

    private String getKakaoId(String idToken) throws JsonProcessingException {
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
