package Project.OpenBook.Domain.Customer.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Dto.CustomerNicknameDto;
import Project.OpenBook.Domain.Customer.Service.CustomerService;
import Project.OpenBook.Jwt.TokenDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "소셜 로그인")
    @GetMapping("login/{providerName}")
    public ResponseEntity<CustomerNicknameDto> socialLogin(
        @PathVariable("providerName") String providerName,
        @RequestParam("code") String code,
        @RequestParam("url") String redirectUrl,
        @RequestParam("protocol") String protocol) throws Exception {
        TokenDto tokenDto = customerService.loginOauth2(providerName, code, redirectUrl, protocol);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenDto.getType() + " " + tokenDto.getAccessToken());
        headers.set("Refresh-Token", tokenDto.getRefreshToken());
        headers.setAccessControlAllowHeaders(Arrays.asList("Authorization", "Refresh-Token"));
        headers.setAccessControlExposeHeaders(Arrays.asList("Authorization", "Refresh-Token"));

        return ResponseEntity.ok()
            .headers(headers)
            .body(new CustomerNicknameDto(tokenDto.getNickname(), tokenDto.getIsNew()));
    }

    /**
     * 회원 탈퇴
     */
    @Operation(summary = "회원 탈퇴")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "성공적인 회원 탈퇴"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 아이디 입력")
    })
    @DeleteMapping("/customers")
    public ResponseEntity<Void> deleteCustomer(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer) {

        customerService.deleteCustomer(customer);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "회원 정보 초기화")
    @DeleteMapping("/customers/reset")
    public ResponseEntity<Void> resetRecord(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer) {
        customerService.resetCustomerRecord(customer);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "회원 약관 동의 여부")
    @PatchMapping("/customers/policy-agree")
    public ResponseEntity<Void> isPolicyAgreed(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer) {
        customerService.isPolicyAgreed(customer);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


}
