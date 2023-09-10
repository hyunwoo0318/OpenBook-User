package Project.OpenBook.Domain;

import Project.OpenBook.Chapter.Domain.Chapter;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;


import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Topic extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String title;

    private Integer startDate;
    private Integer endDate;

    private Boolean startDateCheck;
    private Boolean endDateCheck;

    @ColumnDefault(value = "0")
    private int questionNum;

    @ColumnDefault(value="0")
    private int choiceNum;

    private Integer number;

    @Lob
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    @Setter
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @Setter
    private Category category;

    @OneToMany(mappedBy = "topic", cascade = CascadeType.REMOVE)
    private List<TopicProgress> topicProgressList = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<Keyword> keywordList = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<Sentence> sentenceList = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<PrimaryDate> primaryDateList = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<Choice> choiceList = new ArrayList<>();

    @OneToMany(mappedBy = "topic")
    private List<Description> descriptionList = new ArrayList<>();

    @Builder
    public Topic(String title, Integer startDate, Integer endDate,Boolean startDateCheck, Boolean endDateCheck,
                 int questionNum, int choiceNum, String detail, Chapter chapter, Category category) {
        this.number = 0;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.questionNum = questionNum;
        this.choiceNum = choiceNum;
        this.detail = detail;
        this.chapter = chapter;
        this.category = category;
        this.startDateCheck = startDateCheck;
        this.endDateCheck = endDateCheck;
    }


    public Topic(String title, Chapter chapter) {
        this.title = title;
        this.chapter = chapter;
    }

    public Topic updateTopic(String title, Integer startDate, Integer endDate, Boolean startDateCheck, Boolean endDateCheck,
                             String detail, Chapter chapter, Category category) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.detail = detail;
        this.chapter = chapter;
        this.category = category;
        this.startDateCheck = startDateCheck;
        this.endDateCheck = endDateCheck;
        return this;
    }

    public void updateTopicNumber(Integer number){
        this.number = number;
    }
}
