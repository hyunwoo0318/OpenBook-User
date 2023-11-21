package Project.OpenBook.Domain.Customer.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public interface Oauth2Login {

    public String login(String code, String redirectUrl, String protocol) throws JsonProcessingException, UnsupportedEncodingException;
}
