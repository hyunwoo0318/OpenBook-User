package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "dup_date")
public class DupDate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="answer_topic_id")
    private Topic answerTopic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="description_topic_id")
    private Topic descriptionTopic;

    public DupDate(Topic answerTopic, Topic descriptionTopic) {
        this.answerTopic = answerTopic;
        this.descriptionTopic = descriptionTopic;
    }
}
