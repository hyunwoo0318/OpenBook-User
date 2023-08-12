package Project.OpenBook.Constants;


import org.springframework.beans.factory.annotation.Value;

public abstract class KakaoConst {
    public static final String REDIRECT_URL_LOGIN = "http://localhost:4000/oauth/kakao/login";
    public static final String REQ_URL_TOKEN = "https://kauth.kakao.com/oauth/token";
    public static final String REQ_URL_INFO = "https://kapi.kakao.com/v2/user/me";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    public static String key;
}
