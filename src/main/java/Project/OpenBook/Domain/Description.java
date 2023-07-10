package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Description extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Lob
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @OneToMany(mappedBy = "description", cascade = CascadeType.ALL)
    private List<DupContent> dupContentList = new ArrayList<>();

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
