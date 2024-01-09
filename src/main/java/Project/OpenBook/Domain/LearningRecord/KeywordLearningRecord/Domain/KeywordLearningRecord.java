package Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "keyword_learning_record")
public class KeywordLearningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    @ManyToOne(cascade = CascadeType.REMOVE)
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

    public KeywordLearningRecord(Keyword keyword, Customer customer, Integer answerCount,
        Integer wrongCount) {
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

    public void reset() {
        this.answerCount = 0;
        this.wrongCount = 0;
    }
}
