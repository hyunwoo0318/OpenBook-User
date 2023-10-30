package Project.OpenBook.Domain.LearningRecord.RoundLearningRecord;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Round.Domain.Round;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "round_learning_record")
public class RoundLearningRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "round_id")
    private Round round;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Integer score = 0;

    public RoundLearningRecord(Round round, Customer customer) {
        this.round = round;
        this.customer = customer;
    }

    public void updateScore(Integer score) {
        this.score = score;
    }
}

