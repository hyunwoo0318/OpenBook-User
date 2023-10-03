package Project.OpenBook.Domain.Topic.Domain;

import Project.OpenBook.Domain.Category.Domain.Category;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Domain.Era.Era;
import Project.OpenBook.Domain.TopicPrimaryDate.Domain.TopicPrimaryDate;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.StudyProgress.TopicProgress.Domain.TopicProgress;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
public class Topic extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    private String dateComment;

    @ColumnDefault(value = "0")
    private int questionNum;

    @ColumnDefault(value="0")
    private int choiceNum;

    private Integer number;

    @Lob
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "era_id")
    private Era era;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private List<TopicProgress> topicProgressList = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<Keyword> keywordList = new ArrayList<>();


    @OneToMany(mappedBy = "topic")
    private List<TopicPrimaryDate> topicPrimaryDateList = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<Choice> choiceList = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<Description> descriptionList = new ArrayList<>();

    @Builder
    public Topic(String title, int questionNum, int choiceNum,String dateComment,  String detail, Chapter chapter, Category category, Era era) {
        this.number = 0;
        this.title = title;
        this.questionNum = questionNum;
        this.choiceNum = choiceNum;
        this.dateComment = dateComment;
        this.detail = detail;
        this.chapter = chapter;
        this.category = category;
        this.era = era;
    }


    public Topic(String title, Chapter chapter) {
        this.title = title;
        this.chapter = chapter;
    }

    public Topic(String title) {
        this.title = title;
    }

    public Topic updateTopic(String title, String dateComment, String detail, Chapter chapter, Category category, Era era) {
        this.title = title;
        this.dateComment = dateComment;
        this.detail = detail;
        this.chapter = chapter;
        this.category = category;
        this.era = era;
        return this;
    }

    public void updateTopicNumber(Integer number){
        this.number = number;
    }
}
