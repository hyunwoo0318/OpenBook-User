package Project.OpenBook.Controller;

import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Jwt.TokenDto;
import Project.OpenBook.Jwt.TokenManager;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Arrays;

import static Project.OpenBook.Constants.ErrorCode.INVALID_PARAMETER;

@RestController
@RequiredArgsConstructor
public class JwtController {

    private final TokenManager tokenManager;

    @ApiOperation(value = "refresh-token을 받아 유효한지 체크해서 유효하면 access token 재발급", notes = "request header에 'refresh-token'이라는 필드에 refreshToken값을 넣어서 호출")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "access token 발급"),
            @ApiResponse(responseCode = "400", description = "refresh token이 유효하지 않음")
    })
    @GetMapping("/refresh-token")
    public ResponseEntity<String> checkRefreshToken(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String refreshToken = request.getHeader("Refresh-Token");
        if (refreshToken.isBlank()) {
            throw new CustomException(INVALID_PARAMETER);
        }

        Cookie[] cookies = request.getCookies();

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
