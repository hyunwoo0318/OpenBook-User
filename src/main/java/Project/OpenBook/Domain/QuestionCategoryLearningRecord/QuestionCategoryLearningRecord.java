package Project.OpenBook.Domain.QuestionCategoryLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "question_category_learning_record")
public class QuestionCategoryLearningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "question_category_id")
    private QuestionCategory questionCategory;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer answerCount = 0;

    private Integer wrongCount = 0;

    public QuestionCategoryLearningRecord(QuestionCategory questionCategory, Customer customer) {
        this.questionCategory = questionCategory;
        this.customer = customer;
        answerCount = 0;
        wrongCount = 0;
    }

    public QuestionCategoryLearningRecord(QuestionCategory questionCategory, Customer customer, Integer answerCount, Integer wrongCount) {
        this.questionCategory = questionCategory;
        this.customer = customer;
        this.answerCount = answerCount;
        this.wrongCount = wrongCount;
    }

    public QuestionCategoryLearningRecord updateCount(Integer answerCount, Integer wrongCount) {
        this.answerCount += answerCount;
        this.wrongCount += wrongCount;
        return this;
    }
}
