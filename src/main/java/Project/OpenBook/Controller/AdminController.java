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
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @ApiOperation(value="회원가입", notes="아이디와 비밀번호를 입력받아 회원가입 진행")
    @ApiResponses(value={
            @ApiResponse(responseCode = "201", description = "관리자 회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "관리자 회원가입 실패")
    })
    @PostMapping("/")
    public ResponseEntity adminRegister(@Validated @RequestBody AdminDto adminDto, BindingResult bindingResult) {
        List<ErrorDto> errorDtoList = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        Admin admin = adminService.registerAdmin(adminDto.getLoginId(), adminDto.getPassword());
        if (admin == null) {
            errorDtoList.add(new ErrorDto("loginId", "이미 가입된 아이디입니다. 다른 아이디로 가입을 진행해주세요."));
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "관리자 로그인", notes = "아이디와 비밀번호를 입력받아 관리자 로그인 -> 성공시 SessionId 부여")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/login")
    public ResponseEntity adminLogin(@Validated @RequestBody AdminDto adminDto, BindingResult bindingResult) {

        List<ErrorDto> errorDtoList = new ArrayList<>();

        if (bindingResult.hasErrors()) {
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        Admin admin = adminService.login(adminDto.getLoginId(), adminDto.getPassword());

        if (admin == null) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

}
