package Project.OpenBook.Domain.Topic.Service.dto;

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
    private Integer chapter;

    @NotBlank(message = "상세정보 제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "카테고리를 입력해주세요")
    private String category;

    @NotBlank(message = "시대를 입력해주세요.")
    private String era;

    private String dateComment;


   /* @NotBlank(message = "설명을 입력해주세요.")*/
    private String detail;

    private List<PrimaryDateDto> extraDateList;


    public TopicDetailDto(Topic topic) {
        if (topic.getChapter() != null) {
            this.chapter = topic.getChapter().getNumber();
        }

        this.title = topic.getTitle();

        if (topic.getCategory() != null) {
            this.category = topic.getCategory().getName();
        }

        if (topic.getEra() != null) {
            this.era = topic.getEra().getName();
        }
        this.detail = topic.getDetail();
        this.dateComment = getDateComment();
        this.extraDateList = topic.getTopicPrimaryDateList().stream()
                .map(d -> new PrimaryDateDto(d.getExtraDate(), d.getExtraDateComment()))
                .collect(Collectors.toList());
    }

}
