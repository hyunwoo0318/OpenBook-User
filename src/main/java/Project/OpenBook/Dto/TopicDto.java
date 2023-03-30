package Project.OpenBook.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicDto {

    @Min(value = 1, message = "단원 번호를 입력해주세요.")
    private int chapterNum;

    @NotBlank(message = "상세정보 제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "카테고리를 입력해주세요")
    private String categoryName;

    private Integer startDate;

    private Integer endDate;

    @NotBlank(message = "설명을 입력해주세요.")
    private String detail;

    private List<String> keywordList;
}
