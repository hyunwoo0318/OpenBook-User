package Project.OpenBook.Domain.ExamQuestion.Service.dto;

import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class AnswerNotedQuestionInfoDto {
    private Long id;
    private Integer questionNumber;
    private List<ExamQuestionCommentDto> descriptionCommentList = new ArrayList<>();
    private List<ExamQuestionCommentListDto> correctCommentList = new ArrayList<>();
    private List<ExamQuestionCommentListDto> wrongCommentList = new ArrayList<>();

    public AnswerNotedQuestionInfoDto(ExamQuestion examQuestion,
                                      List<ExamQuestionCommentDto> descriptionCommentList,
                                      List<ExamQuestionCommentListDto> correctCommentList,
                                      List<ExamQuestionCommentListDto> wrongCommentList) {
        this.id = examQuestion.getId();
        this.questionNumber = examQuestion.getNumber();
        this.descriptionCommentList = descriptionCommentList;
        this.correctCommentList = correctCommentList;
        this.wrongCommentList = wrongCommentList;
    }
}
