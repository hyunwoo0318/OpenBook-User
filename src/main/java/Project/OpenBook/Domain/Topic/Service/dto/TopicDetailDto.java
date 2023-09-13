package Project.OpenBook.Domain.Topic.Service.dto;

import Project.OpenBook.Domain.PrimaryDate.Dto.PrimaryDateDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TopicDetailDto {

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


   /* @NotBlank(message = "설명을 입력해주세요.")*/
    private String detail;

    private List<PrimaryDateDto> extraDateList;

    public TopicDetailDto(Topic topic) {
        this.chapter = topic.getChapter().getNumber();
        this.title = topic.getTitle();
        this.category = topic.getCategory().getName();
        this.startDate = topic.getStartDate();
        this.endDate = topic.getEndDate();
        this.startDateCheck = topic.getStartDateCheck();
        this.endDateCheck = topic.getEndDateCheck();
        this.detail = topic.getDetail();
        this.extraDateList = topic.getPrimaryDateList().stream()
                .map(pd -> new PrimaryDateDto(pd.getExtraDate(), pd.getExtraDateCheck(), pd.getExtraDateComment()))
                .collect(Collectors.toList());
    }

}
