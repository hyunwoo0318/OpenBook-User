package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Description extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public Description(String content, Topic topic) {
        this.content = content;
        this.topic = topic;
    }

    public Description updateContent(String content){
        this.content  = content;
        return this;
    }

    public Description updateDescription(String content, Topic topic) {
        this.content = content;
        this.topic = topic;
        return this;
    }
}
