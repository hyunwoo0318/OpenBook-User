package Project.OpenBook.Domain.Description.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeyword;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Description extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    private String comment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @OneToMany(mappedBy = "description")
    private List<DescriptionKeyword> descriptionKeywordList = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_question_id")
    private ExamQuestion examQuestion;

    public Description(String content,String comment,  Topic topic, ExamQuestion examQuestion) {
        this.comment = comment;
        this.content = content;
        this.topic = topic;
        this.examQuestion = examQuestion;
    }

    public Description(ExamQuestion examQuestion) {
        this.examQuestion = examQuestion;
    }

    public Description updateContent(String content, String comment){
        this.content  = content;
        this.comment = comment;
        return this;
    }

    public void updateContent(String s) {
        this.content = s;
    }
}
