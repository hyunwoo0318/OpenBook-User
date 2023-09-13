package Project.OpenBook.Domain.Sentence.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sentence extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public Sentence(String name, Topic topic) {
        this.name = name;
        this.topic = topic;
    }

    public Sentence updateSentence(String name) {
        this.name = name;
        return this;
    }
}
