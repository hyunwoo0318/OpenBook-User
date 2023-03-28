package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Admin;
import Project.OpenBook.Dto.AdminDto;
import Project.OpenBook.Dto.ErrorDto;
import Project.OpenBook.Service.AdminService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    //TODO : ErrorDto로 받아서 넘기고 Validated어노테이션 사용해서 조건 확인하기

    @ApiOperation(value="회원가입", notes="아이디와 비밀번호를 입력받아 회원가입 진행")
    @ApiResponses(value={
            @ApiResponse(responseCode = "201", description = "관리자 회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "관리자 회원가입 실패")
    })
    @PostMapping("/")
    public ResponseEntity adminRegister(@RequestBody AdminDto adminDto) {
        Admin admin = adminService.registerAdmin(adminDto.getLoginId(), adminDto.getPassword());
        if (admin == null) {

            return new ResponseEntity("이미 가입된 아이디입니다. 다른 아이디로 가입을 진행해주세요.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "관리자 로그인", notes = "아이디와 비밀번호를 입력받아 관리자 로그인 -> 성공시 SessionId 부여")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/login")
    public ResponseEntity adminLogin(@RequestBody AdminDto adminDto) {
        Admin admin = adminService.login(adminDto.getLoginId(), adminDto.getPassword());
        if (admin == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication = {}", authentication);
        return new ResponseEntity(HttpStatus.OK);
    }

}
