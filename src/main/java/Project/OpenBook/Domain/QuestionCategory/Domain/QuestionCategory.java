package Project.OpenBook.Domain.QuestionCategory.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Category.Domain.Category;
import Project.OpenBook.Domain.Era.Era;
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
public class QuestionCategory extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "era_id")
    private Era era;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionCategory")
    private List<Topic> topicList = new ArrayList<>();
    public QuestionCategory(String title, Category category, Era era) {
        this.title = title;
        this.category = category;
        this.era = era;
        this.number = 1000;
    }

    public void updateQuestionCategory(String title, Category category, Era era) {
        this.title = title;
        this.category = category;
        this.era = era;
    }

    public void updateNumber(Integer number) {
        this.number =number;
    }
}
