package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;


import java.time.LocalDateTime;
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
    @ColumnDefault(value = "10000")
    private LocalDateTime startDate;
    @ColumnDefault(value = "10000")
    private LocalDateTime endDate;
    @ColumnDefault(value = "0")
    private int questionNum;

    @ColumnDefault(value="0")
    private int choiceNum;

    @Lob
    private String detail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "topic")
    private List<Choice> choiceList = new ArrayList<>();

    @Builder
    public Topic(String title, LocalDateTime startDate, LocalDateTime endDate, int questionNum, int choiceNum, String detail, Chapter chapter, Category category) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.questionNum = questionNum;
        this.choiceNum = choiceNum;
        this.detail = detail;
        this.chapter = chapter;
        this.category = category;

    }

    public Topic updateTopic(String title,LocalDateTime startDate,LocalDateTime endDate, String detail, Chapter chapter, Category category) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.detail = detail;
        this.chapter = chapter;
        this.category = category;
        return this;
    }

    public void deleteChapter() {
        this.chapter = null;
    }

    public void deleteCategory() {
        this.category = null;
    }

    public void changeChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public void changeCategory(Category category) {
        this.category = category;
    }
}
