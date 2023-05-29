package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Dto.customer.CustomerDetailDto;
import Project.OpenBook.Dto.error.ErrorDto;
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
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final BookmarkService bookmarkService;
    private final AnswerNoteService answerNoteService;
    private final OAuthService oauthService;
    private final InMemoryClientRegistrationRepository clientRegistrationRepository;

    @ApiOperation("회원 추가정보 입력")
    @ApiResponses(value={
           @ApiResponse(responseCode = "200", description = "셩공적인 추가정보 입력"),
            @ApiResponse(responseCode = "400", description = "잘못된 정보 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회원에 대한 정보 입력"),
            @ApiResponse(responseCode = "409", description = "중복된 닉네임 입력")
    })
    @PostMapping("customers/{customerId}/details")
    public ResponseEntity addDetails(@PathVariable("customerId") Long customerId,
                                     @Validated @RequestBody CustomerDetailDto customerDetailDto){
        Customer customer = customerService.addDetails(customerId, customerDetailDto);

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

    @GetMapping("/login/oauth2/code/{provider}")
    public ResponseEntity<Object> loginKakao(@PathVariable("provider")String provider, @RequestParam("code") String code,
                                             HttpServletResponse response) {
        TokenDto tokenDto = oauthService.login(provider, code);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", tokenDto.getType() + " " + tokenDto.getAccessToken());
        headers.add("Refresh-token", tokenDto.getRefreshToken());

        return ResponseEntity.ok()
                .headers(headers).build();


    }

}
