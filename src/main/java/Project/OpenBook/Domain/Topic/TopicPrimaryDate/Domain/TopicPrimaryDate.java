package Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TopicPrimaryDate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer extraDate;

    private String extraDateComment;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public TopicPrimaryDate(Integer extraDate, String extraDateComment, Topic topic) {
        this.extraDate = extraDate;
        this.extraDateComment = extraDateComment;
        this.topic = topic;
    }
}
