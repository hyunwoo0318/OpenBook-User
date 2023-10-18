package Project.OpenBook.Domain.StudyProgress.KeywordLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

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

    private Integer totalCount = 0;

    private Integer wrongCount = 0;

    public KeywordLearningRecord(Keyword keyword, Customer customer) {
        this.keyword = keyword;
        this.customer = customer;
        totalCount = 0;
        wrongCount = 0;
    }

    public KeywordLearningRecord updateCount(Integer totalCount, Integer wrongCount) {
        this.totalCount += totalCount;
        this.wrongCount += wrongCount;
        return this;
    }
}
