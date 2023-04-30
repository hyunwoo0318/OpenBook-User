package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public Choice(String content, Topic topic) {
        this.content = content;
        this.topic = topic;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public Choice updateChoice(String content,Topic topic) {
        this.content = content;
        this.topic = topic;
        return this;
    }
}
