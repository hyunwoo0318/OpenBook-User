package Project.OpenBook.Domain.Choice.Domain;

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
public class Choice extends BaseEntity {

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
