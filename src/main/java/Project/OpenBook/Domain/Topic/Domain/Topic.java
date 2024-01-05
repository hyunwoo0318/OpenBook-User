package Project.OpenBook.Domain.Topic.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContent;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.TopicPrimaryDate.Domain.TopicPrimaryDate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Entity
@Getter
@NoArgsConstructor
public class Topic extends BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @Field(type = FieldType.Text)
    private String title;

    private String dateComment;

    @ColumnDefault(value = "0")
    private int questionNum;

    @ColumnDefault(value = "0")
    private int choiceNum;

    private Integer number;


    @Lob
    @Field(type = FieldType.Text)
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_category_id")
    private QuestionCategory questionCategory;

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
    private List<Keyword> keywordList = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<TopicPrimaryDate> topicPrimaryDateList = new ArrayList<>();

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
    private List<Choice> choiceList = new ArrayList<>();

    @OneToMany(mappedBy = "topic", fetch = FetchType.LAZY)
    private List<Description> descriptionList = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<TopicLearningRecord> topicLearningRecordList = new ArrayList<>();

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<JJHContent> jjhContentList = new ArrayList<>();

    @Builder
    public Topic(Integer number, String title, int questionNum, int choiceNum, String dateComment,
        String detail, Chapter chapter, QuestionCategory questionCategory) {
        this.number = number;
        this.title = title;
        this.questionNum = questionNum;
        this.choiceNum = choiceNum;
        this.dateComment = dateComment;
        this.detail = detail;
        this.chapter = chapter;
        this.questionCategory = questionCategory;
    }


    public Topic(String title, Chapter chapter) {
        this.title = title;
        this.chapter = chapter;
    }

    public Topic(String title) {
        this.title = title;
    }


}
