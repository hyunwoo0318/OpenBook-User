package Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "topic_learning_record")
public class TopicLearningRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public TopicLearningRecord(Topic topic, Customer customer, Integer answerCount, Integer wrongCount) {
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
}
