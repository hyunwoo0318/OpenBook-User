package Project.OpenBook.Domain.QuestionCategory.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Category.Domain.Category;
import Project.OpenBook.Domain.Era.Era;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuestionCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer number;

    private Integer totalQuestionProb;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "era_id")
    private Era era;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionCategory")
    private List<Topic> topicList = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "questionCategory", cascade = CascadeType.REMOVE)
    private List<QuestionCategoryLearningRecord> questionCategoryLearningRecordList = new ArrayList<>();

    public QuestionCategory(String title, Category category, Era era) {
        this.totalQuestionProb = 0;
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
        this.number = number;
    }

    public void updateTotalQuestionProb(Integer totalQuestionProb) {
        this.totalQuestionProb = totalQuestionProb;
    }

}
