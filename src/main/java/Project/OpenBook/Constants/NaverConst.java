package Project.OpenBook.Constants;

import org.springframework.beans.factory.annotation.Value;

public abstract class NaverConst {
    public static final String REDIRECT_URL_LOGIN = "http://localhost:8080/customer-login/kakao";
    public static final String REQ_URL_TOKEN = "https://nid.naver.com/oauth2.0/token";
    public static final String REQ_URL_INFO = "https://openapi.naver.com/v1/nid/me";

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    public static String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    public static String CLIENT_SECRET;
}
