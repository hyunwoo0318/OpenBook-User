package Project.OpenBook.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(name = "AdminDto" , description = "관리자 로그인/회원가입 Dto")
@Data
public class AdminDto {
    @Schema(description = "관리자 로그인 아이디", example = "adminId")
    @NotBlank(message = "로그인 아이디를 입력해주세요.")
    private String loginId;

    @Schema(description = "관리자 비밀번호", example = "adminPassword")
    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;
}
