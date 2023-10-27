package Project.OpenBook.Domain.Topic.Service.dto;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryIdDto;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryIdTitleDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicAddUpdateDto {
    @Min(value = 1, message = "단원 번호를 입력해주세요.")
    private Integer chapter;

    @NotBlank(message = "상세정보 제목을 입력해주세요.")
    private String title;

    private QuestionCategoryIdDto questionCategory;

    private String dateComment;


    /* @NotBlank(message = "설명을 입력해주세요.")*/
    private String detail;
    private Integer number;

    private List<PrimaryDateDto> extraDateList;



}
