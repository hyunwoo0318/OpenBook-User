package Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "keyword_learning_record")
public class KeywordLearningRecord {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer answerCount = 0;

    private Integer wrongCount = 0;

    public KeywordLearningRecord(Keyword keyword, Customer customer) {
        this.keyword = keyword;
        this.customer = customer;
        this.answerCount = 0;
        this.wrongCount = 0;
    }

    public KeywordLearningRecord(Keyword keyword, Customer customer, Integer answerCount, Integer wrongCount) {
        this.keyword = keyword;
        this.customer = customer;
        this.answerCount = answerCount;
        this.wrongCount = wrongCount;
    }

    public KeywordLearningRecord updateCount(Integer answerCount, Integer wrongCount) {
        this.answerCount += answerCount;
        this.wrongCount += wrongCount;
        return this;
    }
}
