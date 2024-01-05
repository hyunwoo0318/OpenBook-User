package Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "topic_learning_record")
public class TopicLearningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Boolean isBookmarked = false;

    private Integer answerCount = 0;

    private Integer wrongCount = 0;

    public TopicLearningRecord(Topic topic, Customer customer) {
        this.isBookmarked = false;
        this.topic = topic;
        this.customer = customer;
        answerCount = 0;
        wrongCount = 0;
    }

    public TopicLearningRecord(Topic topic, Customer customer, Integer answerCount,
        Integer wrongCount) {
        this.isBookmarked = false;
        this.topic = topic;
        this.customer = customer;
        this.answerCount = answerCount;
        this.wrongCount = wrongCount;
    }

    public TopicLearningRecord updateCount(Integer answerCount, Integer wrongCount) {
        this.answerCount += answerCount;
        this.wrongCount += wrongCount;
        return this;
    }

    public void updateBookmark(Boolean isBookmarked) {
        this.isBookmarked = isBookmarked;
    }

    public void reset() {
        this.isBookmarked = false;
        this.answerCount = 0;
        this.wrongCount = 0;
    }
}
