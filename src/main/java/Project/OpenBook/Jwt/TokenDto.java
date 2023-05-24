package Project.OpenBook.Jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@AllArgsConstructor
@Data
public class TokenDto {
    private String type;
    private String accessToken;
    private String refreshToken;
}
