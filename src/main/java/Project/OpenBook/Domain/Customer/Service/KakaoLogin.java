package Project.OpenBook.Domain.Customer.Service;

import Project.OpenBook.Handler.Exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

import static Project.OpenBook.Constants.ErrorCode.LOGIN_FAIL;

@RequiredArgsConstructor
public class KakaoLogin implements Oauth2Login{
    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    private String kakaoKey = "ca80f14a6e6b6c34ea821c46af0cc10c";
    @Override
    public String login(String code, String redirectURL, String protocol) throws JsonProcessingException {
        if (protocol == null || protocol.equals("undefined")) {
            protocol = "http";
        }
        String kakaoUri = protocol + "://" + redirectURL;
        Map<String, String> map = webClientBuilder.build()
                .post()
                .uri(uriBuilder -> uriBuilder
                        .scheme("https")
                        .host("kauth.kakao.com")
                        .path("/oauth/token")
                        .queryParam("grant_type", "authorization_code")
                        .queryParam("client_id", kakaoKey)
                        .queryParam("redirect_uri", kakaoUri)
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

        Map<String, String> bodyMap = objectMapper.readValue(decodedBody, Map.class);

        return bodyMap.get("sub");
    }
}
