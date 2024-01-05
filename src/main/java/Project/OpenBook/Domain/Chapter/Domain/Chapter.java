package Project.OpenBook.Domain.Chapter.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContent;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chapter extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<JJHList> jjhLists = new ArrayList<>();

    @OneToMany(mappedBy = "chapter", fetch = FetchType.LAZY)
    private List<Topic> topicList = new ArrayList<>();


}
