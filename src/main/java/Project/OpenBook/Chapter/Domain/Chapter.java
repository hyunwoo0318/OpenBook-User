package Project.OpenBook.Chapter.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.ChapterProgress;
import Project.OpenBook.Domain.ChapterSection;
import Project.OpenBook.Domain.Topic;
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

    private Integer startDate;
    private Integer endDate;


    public Chapter(String title, int number) {
        this.title = title;
        this.number = number;
    }

    public Chapter(int number, String title, Integer startDate, Integer endDate) {
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
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



    public Chapter updateChapter(String title, int number, Integer startDate, Integer endDate) {
        this.title = title;
        this.number = number;
        this.startDate = startDate;
        this.endDate = endDate;
        return this;
    }

    public Chapter updateContent(String content) {
        this.content = content;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Chapter)) return false;

        Chapter chapter = (Chapter) o;

        if (number != chapter.number) return false;
        if (!Objects.equals(id, chapter.id)) return false;
        if (!Objects.equals(content, chapter.content)) return false;
        if (!Objects.equals(startDate, chapter.startDate)) return false;
        if (!Objects.equals(endDate, chapter.endDate)) return false;
        if (!Objects.equals(title, chapter.title)) return false;
        if (!Objects.equals(chapterSectionList, chapter.chapterSectionList))
            return false;
        return Objects.equals(chapterProgressList, chapter.chapterProgressList);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        result = 31 * result + number;
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (chapterSectionList != null ? chapterSectionList.hashCode() : 0);
        result = 31 * result + (chapterProgressList != null ? chapterProgressList.hashCode() : 0);
        return result;
    }
}
