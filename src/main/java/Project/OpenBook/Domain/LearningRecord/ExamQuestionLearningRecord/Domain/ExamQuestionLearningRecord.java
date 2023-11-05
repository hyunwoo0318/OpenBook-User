package Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "exam_question_learning_record")
public class ExamQuestionLearningRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_question_id")
    private ExamQuestion examQuestion;

    private Boolean answerNoted;

    private Boolean firstSolve;

    private Integer checkedNumber;

    private Integer score;

    public ExamQuestionLearningRecord(Customer customer, ExamQuestion examQuestion) {
        this.firstSolve = true;
        this.answerNoted = false;
        this.score = 0;
        this.customer = customer;
        this.examQuestion = examQuestion;
    }

    public void updateAnswerNoted(boolean answerNoted) {
        if (answerNoted == false) {
            this.firstSolve = false;
        }
        this.answerNoted = answerNoted;
    }

    public void updateInfo(Integer checkedNumber, Integer score) {
        this.score = score;
        this.checkedNumber = checkedNumber;
    }
}
