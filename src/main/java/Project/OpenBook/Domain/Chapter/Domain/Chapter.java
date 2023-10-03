package Project.OpenBook.Domain.Chapter.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;
import Project.OpenBook.Domain.StudyProgress.ChapterSection.Domain.ChapterSection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chapter extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    @Column(nullable = false, unique = true)
    private int number;

//    private Integer startDate;
//    private Integer endDate;
    private String dateComment;


    public Chapter(String title, int number) {
        this.title = title;
        this.number = number;
    }

//    public Chapter(int number, String title, Integer startDate, Integer endDate) {
//        this.number = number;
//        this.startDate = startDate;
//        this.endDate = endDate;
//        this.title = title;
//    }


    public Chapter(int number, String dateComment, String title) {
        this.number = number;
        this.dateComment = dateComment;
        this.title = title;
    }

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "chapter")
    private List<Topic> topicList = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.REMOVE)
    private List<ChapterSection> chapterSectionList = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.REMOVE)
    private List<ChapterProgress> chapterProgressList = new ArrayList<>();



    public Chapter updateChapter(String title, int number, String dateComment) {
        this.title = title;
        this.number = number;
        this.dateComment = dateComment;
        return this;
    }

    public Chapter updateContent(String content) {
        this.content = content;
        return this;
    }

}
