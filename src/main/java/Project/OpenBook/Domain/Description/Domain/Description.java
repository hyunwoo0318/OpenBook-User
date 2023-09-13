package Project.OpenBook.Domain.Description.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Description extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @Lob
    private String content;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @OneToOne
    private ExamQuestion examQuestion;

    public Description(String content,String comment,  Topic topic, ExamQuestion examQuestion) {
        this.comment = comment;
        this.content = content;
        this.topic = topic;
        this.examQuestion = examQuestion;
    }

    public Description updateContent(String content, String comment){
        this.content  = content;
        this.comment = comment;
        return this;
    }
}
