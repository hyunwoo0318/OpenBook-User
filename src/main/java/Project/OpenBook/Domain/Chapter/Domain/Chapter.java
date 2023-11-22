package Project.OpenBook.Domain.Chapter.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContent;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
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
public class Chapter extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    private String content;

    @Column(nullable = false, unique = true)
    private int number;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<JJHContent> jjhContentList = new ArrayList<>();


    private String dateComment;


    public Chapter(String title, int number) {
        this.title = title;
        this.number = number;
    }

    public Chapter(int number, String dateComment, String title) {
        this.number = number;
        this.dateComment = dateComment;
        this.title = title;
    }

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "chapter",cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<JJHList> jjhLists = new ArrayList<>();

    @OneToMany(mappedBy = "chapter",fetch = FetchType.LAZY)
    private List<Topic> topicList = new ArrayList<>();


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

    public Chapter updateNumber(Integer number) {
        this.number = number;
        return this;
    }


}
