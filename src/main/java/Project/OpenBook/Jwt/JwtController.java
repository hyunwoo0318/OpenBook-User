package Project.OpenBook.Jwt;

import Project.OpenBook.Handler.Exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

import java.util.Arrays;

import static Project.OpenBook.Constants.ErrorCode.LOGIN_FAIL;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final TokenManager tokenManager;

    @Operation(summary = "refresh-token을 받아 유효한지 체크해서 유효하면 access token 재발급", description = "request header에 'refresh-token'이라는 필드에 refreshToken값을 넣어서 호출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "access token 발급"),
            @ApiResponse(responseCode = "400", description = "refresh token이 유효하지 않음")
    })
    @GetMapping("/refresh-token")
    public ResponseEntity<String> checkRefreshToken(HttpServletRequest request) {
        String refreshToken = request.getHeader("Refresh-Token");
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new CustomException(LOGIN_FAIL);
        }

        tokenManager.validateToken(refreshToken);
        TokenDto tokenDto = tokenManager.generateToken(refreshToken);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenDto.getType() + " " + tokenDto.getAccessToken());
        headers.set("Refresh-Token", tokenDto.getRefreshToken());
        headers.setAccessControlAllowHeaders(Arrays.asList("Authorization", "Refresh-Token"));
        headers.setAccessControlExposeHeaders(Arrays.asList("Authorization", "Refresh-Token"));


        ResponseEntity<String> responseEntity = ResponseEntity.ok()
                .headers(headers)
                .body("Login Success!!");

        return responseEntity;
    }
}
