package Project.OpenBook.Domain.JJH.JJHListProgress;

import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "jjh_list_progress",
        uniqueConstraints={
                @UniqueConstraint(
                        columnNames={"customer_id", "jjh_list_id"}
                )
        })
public class JJHListProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jjh_list_id")
    private JJHList jjhList;

    @Enumerated(EnumType.STRING)
    private StateConst state;



    public JJHListProgress(Customer customer, JJHList jjhList) {
        this.state = StateConst.LOCKED;
        this.customer = customer;
        this.jjhList = jjhList;
    }

    public JJHListProgress(Customer customer, JJHList jjhList, StateConst state) {
        this.customer = customer;
        this.jjhList = jjhList;
        this.state = state;
    }


    public void updateState(StateConst state) {
        this.state = state;
    }

    public void reset() {
        if (this.jjhList.getNumber() == 1) {
            this.state = StateConst.IN_PROGRESS;
        }else{
            this.state = StateConst.LOCKED;
        }
    }
}
