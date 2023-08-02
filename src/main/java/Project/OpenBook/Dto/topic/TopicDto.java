package Project.OpenBook.Dto.topic;

import Project.OpenBook.Dto.PrimaryDateDto;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicDto {

    @Min(value = 1, message = "단원 번호를 입력해주세요.")
    private int chapter;

    @NotBlank(message = "상세정보 제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "카테고리를 입력해주세요")
    private String category;

    private Integer startDate;

    private Boolean startDateCheck;

    private Boolean endDateCheck;

    private Integer endDate;

    private Integer number;

   /* @NotBlank(message = "설명을 입력해주세요.")*/
    private String detail;

    private List<PrimaryDateDto> extraDateList;

}
