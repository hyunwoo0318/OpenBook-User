package Project.OpenBook.Domain;

import Project.OpenBook.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Choice extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String content;

    private String comment;

    private String type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_question_id")
    private ExamQuestion examQuestion;

    @OneToMany(mappedBy = "choice", cascade = CascadeType.ALL)
    private List<DupContent> dupContentList = new ArrayList<>();

    public Choice(String type,String content, String comment, Topic topic, ExamQuestion examQuestion) {
        this.type = type;
        this.content = content;
        this.comment = comment;
        this.topic = topic;
        this.examQuestion = examQuestion;
    }

    public Choice updateChoice(String content,String comment, Topic topic) {
        this.content = content;
        this.comment = comment;
        this.topic = topic;
        return this;
    }
}
