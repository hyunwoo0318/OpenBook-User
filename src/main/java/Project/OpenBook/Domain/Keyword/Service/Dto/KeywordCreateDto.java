package Project.OpenBook.Domain.Keyword.Service.Dto;

import Project.OpenBook.Domain.Topic.Service.dto.PrimaryDateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordCreateDto {


    @NotBlank(message = "키워드 이름을 입력해주세요.")
    private String name;
    private String comment;
    @NotBlank(message = "토픽제목을 입력해주세요.")
    private String topic;
    private String file;
    private String dateComment;
    private Integer number;
    private List<PrimaryDateDto> extraDateList = new ArrayList<>();
}
