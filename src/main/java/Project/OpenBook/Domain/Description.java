package Project.OpenBook.Domain;

import Project.OpenBook.ExamQuestion.ExamQuestion;
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
