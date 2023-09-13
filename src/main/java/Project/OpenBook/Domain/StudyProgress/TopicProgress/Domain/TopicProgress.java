package Project.OpenBook.Domain.StudyProgress.TopicProgress.Domain;

import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "topic_progress")
public class TopicProgress  extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private LocalDateTime lastStudyTime;

    @ColumnDefault(value = "0")
    private Integer wrongCount = 0;

    private String state = StateConst.LOCKED.getName();

    public TopicProgress(Customer customer,Topic topic) {
        this.customer = customer;
        this.topic = topic;
    }

    public TopicProgress(Customer customer, Topic topic,Integer wrongCount, String state) {
        this.customer = customer;
        this.topic = topic;
        this.wrongCount = wrongCount;
        this.state = state;
    }
    public TopicProgress updateLastStudyTime() {
        this.lastStudyTime = LocalDateTime.now();
        return this;
    }

    public TopicProgress updateWrongCount(int wrongCount) {
        this.wrongCount += wrongCount;
        return this;
    }

    public void updateState(String state) {
        this.state = state;
    }
}
