package Project.OpenBook.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Schema(name = "AdminDto" , description = "관리자 로그인/회원가입 Dto")
@Data
public class AdminDto {
    @Schema(description = "관리자 로그인 아이디", example = "adminId")
    private String loginId;

    @Schema(description = "관리자 비밀번호", example = "adminPassword")
    private String password;
}
