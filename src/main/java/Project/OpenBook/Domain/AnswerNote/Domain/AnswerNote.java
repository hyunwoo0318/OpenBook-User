package Project.OpenBook.Domain.AnswerNote.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Question.Domain.Question;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnswerNote extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    public AnswerNote(Customer customer,Question question) {
        this.customer = customer;
        this.question = question;
    }
}
