package Project.OpenBook.Domain.JJH.JJHContentProgress;

import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "jjh_content_progress",
        uniqueConstraints={
        @UniqueConstraint(
                columnNames={"customer_id", "jjh_content_id"}
        )
})
public class JJHContentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jjh_content_id")
    private JJHContent jjhContent;

    @Enumerated(EnumType.STRING)
    private StateConst state;

    public JJHContentProgress(Customer customer, JJHContent jjhContent) {
        this.state = StateConst.LOCKED;
        this.customer = customer;
        this.jjhContent = jjhContent;
    }

    public JJHContentProgress(Customer customer, JJHContent jjhContent, StateConst state) {
        this.customer = customer;
        this.jjhContent = jjhContent;
        this.state = state;
    }

    public void updateState(StateConst state) {
        this.state = state;
    }

    public void reset(){
        if (jjhContent.getNumber() == 1) {
            this.state = StateConst.IN_PROGRESS;
        }else{
            this.state = StateConst.LOCKED;
        }
    }
}
