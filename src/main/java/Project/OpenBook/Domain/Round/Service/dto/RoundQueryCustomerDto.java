package Project.OpenBook.Domain.Round.Service.dto;

import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecord;
import Project.OpenBook.Domain.Round.Domain.Round;
import lombok.Getter;

import javax.validation.constraints.Min;

@Getter
public class RoundQueryCustomerDto {
    @Min(value = 1, message = "회차 번호를 입력해주세요.")
    private Integer number;
    @Min(value = 1, message = "회차 년도를 입력해주세요.")
    private Integer date;
    private Integer score;

    public RoundQueryCustomerDto(RoundLearningRecord record) {
        Round round = record.getRound();
        this.number = round.getNumber();
        this.date =round.getDate();
        this.score = record.getScore();
    }

}
