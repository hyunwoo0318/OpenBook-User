package Project.OpenBook.Domain.Topic.Service.dto;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.QuestionCategory.Service.Dto.QuestionCategoryIdTitleDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TopicDetailDto {

    @Min(value = 1, message = "단원 번호를 입력해주세요.")
    private Integer chapter;

    @NotBlank(message = "상세정보 제목을 입력해주세요.")
    private String title;

    private QuestionCategoryIdTitleDto questionCategory;

    private String dateComment;


   /* @NotBlank(message = "설명을 입력해주세요.")*/
    private String detail;
    private Integer number;

    private List<PrimaryDateDto> extraDateList;


    public TopicDetailDto(Topic topic) {
        if (topic.getChapter() != null) {
            this.chapter = topic.getChapter().getNumber();
        }

        this.title = topic.getTitle();

        QuestionCategory findQuestionCategory = topic.getQuestionCategory();
        if (findQuestionCategory != null) {
            this.questionCategory = new QuestionCategoryIdTitleDto(findQuestionCategory.getId(), findQuestionCategory.getTitle());
        }

        this.detail = topic.getDetail();
        this.dateComment = topic.getDateComment();
        this.extraDateList = topic.getTopicPrimaryDateList().stream()
                .map(d -> new PrimaryDateDto(d.getExtraDate(), d.getExtraDateComment()))
                .collect(Collectors.toList());
        this.number = topic.getNumber();
    }

}
