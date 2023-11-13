package Project.OpenBook.Domain.Choice.Domain;

import Project.OpenBook.Constants.ChoiceType;
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

    private String content;

    private String comment;

    private Integer number;

    @Enumerated(EnumType.STRING)
    private ChoiceType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_question_id")
    private ExamQuestion examQuestion;

    public Choice(ChoiceType type,String content, String comment, Topic topic, ExamQuestion examQuestion) {
        this.type = type;
        this.content = content;
        this.comment = comment;
        this.topic = topic;
        this.examQuestion = examQuestion;
    }

    public Choice(Integer number, String content, ChoiceType type, ExamQuestion examQuestion) {
        this.number = number;
        this.content = content;
        this.type = type;
        this.examQuestion = examQuestion;
    }

    public Choice updateChoice(String content, String comment, Topic topic) {
        this.content = content;
        this.comment = comment;
        this.topic = topic;
        return this;
    }

    public Choice updateChoice(Integer number, String content){
        this.number = number;
        this.content = content;
        return this;
    }

    public void updateContent(String content) {
        this.content = content;
    }
}
