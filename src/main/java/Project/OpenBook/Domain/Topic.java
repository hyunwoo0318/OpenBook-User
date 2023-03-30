package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

import static Project.OpenBook.Service.TopicService.NO_RECORD;

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
    private Integer startDate;
    @ColumnDefault(value = "10000")
    private Integer endDate;
    @ColumnDefault(value = "0")
    private int questionNum;

    @ColumnDefault(value="0")
    private int choiceNum;
    private String detail;

    private String keywords;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public Topic(String title, int startDate, int endDate, int questionNum, int choiceNum, String detail, Chapter chapter, Category category,String keywords) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.questionNum = questionNum;
        this.choiceNum = choiceNum;
        this.detail = detail;
        this.chapter = chapter;
        this.category = category;
        this.keywords = keywords;
    }

    public Topic updateTopic(String title, int startDate,int endDate, String detail, Chapter chapter, Category category, String keywords) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.detail = detail;
        this.chapter = chapter;
        this.category = category;
        this.keywords = keywords;
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
