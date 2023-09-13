package Project.OpenBook.Config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * swagger api 문서를 사용하기 위한 설정
 */
@Configuration
public class SwaggerConfig{

    @Bean
    public OpenAPI openAPI() {

        Info info = new Info()

                .version("v1.0.0")
                .title("OpenBook Project")
                .description("OpenBook Api 명세서입니다.");

        return new OpenAPI()
                .info(info);

    }
}
