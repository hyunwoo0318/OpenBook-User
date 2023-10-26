package Project.OpenBook.Domain.TimelineLearningRecord.Domain;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "timeline_learning_record")
public class TimelineLearningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "timeline_id")
    private Timeline timeline;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer answerCount = 0;

    private Integer wrongCount = 0;

    public TimelineLearningRecord(Timeline timeline, Customer customer) {
        this.timeline = timeline;
        this.customer = customer;
        answerCount = 0;
        wrongCount = 0;
    }

    public TimelineLearningRecord(Timeline timeline, Customer customer, Integer answerCount, Integer wrongCount) {
        this.timeline = timeline;
        this.customer = customer;
        this.answerCount = answerCount;
        this.wrongCount = wrongCount;
    }

    public TimelineLearningRecord updateCount(Integer answerCount, Integer wrongCount) {
        this.answerCount += answerCount;
        this.wrongCount += wrongCount;
        return this;
    }
}
