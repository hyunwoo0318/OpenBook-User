package Project.OpenBook.Constants;


import org.springframework.beans.factory.annotation.Value;

public abstract class KakaoConst {
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    public static String key;
}
