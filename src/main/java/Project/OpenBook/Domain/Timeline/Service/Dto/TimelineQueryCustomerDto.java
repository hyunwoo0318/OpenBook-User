package Project.OpenBook.Domain.Timeline.Service.Dto;

import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.TimelineLearningRecord.Domain.TimelineLearningRecord;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TimelineQueryCustomerDto {
    private String title;
    private String era;
    private Integer startDate;
    private Integer endDate;
    private Long id;
    private Integer score;

    public TimelineQueryCustomerDto(TimelineLearningRecord record) {
        Timeline timeline = record.getTimeline();
        this.title = timeline.getTitle();
        this.era = timeline.getEra().getName();
        this.startDate = timeline.getStartDate();
        this.endDate = timeline.getEndDate();
        this.id = timeline.getId();
        this.score = (record.getAnswerCount() - record.getWrongCount());
    }
}
