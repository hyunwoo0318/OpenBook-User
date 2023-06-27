package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Dto.customer.CustomerAddDetailDto;
import Project.OpenBook.Dto.customer.CustomerCodeList;
import Project.OpenBook.Dto.customer.CustomerDetailDto;
import Project.OpenBook.Jwt.TokenDto;
import Project.OpenBook.Service.AnswerNoteService;
import Project.OpenBook.Service.BookmarkService;
import Project.OpenBook.Service.CustomerService;
import Project.OpenBook.Service.OAuthService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final BookmarkService bookmarkService;
    private final AnswerNoteService answerNoteService;
    private final OAuthService oauthService;

    @ApiOperation("회원 추가정보 입력")
    @ApiResponses(value={
           @ApiResponse(responseCode = "200", description = "셩공적인 추가정보 입력"),
            @ApiResponse(responseCode = "400", description = "잘못된 정보 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원에 대한 정보 입력"),
            @ApiResponse(responseCode = "409", description = "중복된 닉네임 입력")
    })
    @PostMapping("customers/{customerId}/details")
    public ResponseEntity addDetails(@PathVariable("customerId") Long customerId,
                                     @Validated @RequestBody CustomerAddDetailDto customerAddDetailDto){
        Customer customer = customerService.addDetails(customerId, customerAddDetailDto);

        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation("특정 회원의 북마크 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 아이디 입력")
    })
    @GetMapping("customers/{customerId}/bookmarks")
    public ResponseEntity queryBookmarks(@PathVariable("customerId") Long customerId){
        List<String> titleList = bookmarkService.queryBookmarks(customerId);

        return new ResponseEntity(titleList, HttpStatus.OK);
    }

    @ApiOperation("특정 회원의 오답노트 리스트 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 아이디 입력")
    })
    @GetMapping("customers/{customerId}/answer-notes")
    public ResponseEntity queryAnswerNotes(@PathVariable("customerId") Long customerId){
        List<Long> questionIdList = answerNoteService.queryAnswerNotes(customerId);
        return new ResponseEntity(questionIdList, HttpStatus.OK);
    }

    @GetMapping("login/{providerName}")
    public ResponseEntity<Long> loginKakao(@PathVariable("providerName") String providerName,@RequestParam("code") String code,
                                             HttpServletResponse response) {
        TokenDto tokenDto = oauthService.login(providerName, code);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", tokenDto.getType() + " " + tokenDto.getAccessToken());
        headers.set("Refresh-token", tokenDto.getRefreshToken());

        ResponseEntity<Long> responseEntity = ResponseEntity.ok()
                .headers(headers)
                .body(tokenDto.getCustomerId());
        return responseEntity;
    }

    /**
     * 관리자 페이지에서 회원 정보 관리
     */

    @ApiOperation("모든 회원의 식별코드 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 회원의 식별코드 조회 성공")
    })
    @GetMapping("/admin/customers")
    public ResponseEntity queryCustomers(){
        //TODO : DTO 만들
        CustomerCodeList customerCodeList = customerService.queryCustomers();
        return new ResponseEntity<>(customerCodeList, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 회원의 정보 조회", notes = "[닉네임, 최초 학습 수준, 나이, 유저 로그, 유저 구독 정보] 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원의 정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원 식별코드 입력")
    })
    @GetMapping("/admin/customers/{code}")
    public ResponseEntity queryCustomerDetail(@PathVariable("code") String code) {
        CustomerDetailDto customerDetailDto = customerService.queryCustomerDetail(code);
        return new ResponseEntity(customerDetailDto, HttpStatus.OK);
    }

}
