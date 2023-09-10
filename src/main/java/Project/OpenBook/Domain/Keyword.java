package Project.OpenBook.Domain;

import Project.OpenBook.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    private String comment;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public Keyword(String name, String comment, Topic topic, String imageUrl) {
        this.name = name;
        this.comment = comment;
        this.topic = topic;
        this.imageUrl = imageUrl;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public Keyword updateKeyword(String name, String comment,String imageUrl) {
        this.name = name;
        this.comment = comment;
        this.imageUrl = imageUrl;
        return this;
    }
}
