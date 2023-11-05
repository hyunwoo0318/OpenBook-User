package Project.OpenBook.Domain.StudyHistory.Service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExamQuestionRecordDto {
    private Long id;
    private Integer checkedChoiceKey;
    private Integer score;
}
