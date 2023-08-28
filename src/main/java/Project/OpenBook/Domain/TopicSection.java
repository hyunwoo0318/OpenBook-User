package Project.OpenBook.Domain;

import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.StateConst;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "topic_section")
public class TopicSection extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private String content = ContentConst.NOT_STARTED.getName();

    private String state = StateConst.LOCKED.getName();

    public TopicSection(Customer customer, Topic topic) {
        this.customer = customer;
        this.topic = topic;
    }

    public TopicSection(Customer customer, Topic topic, String content, String state) {
        this.customer = customer;
        this.topic = topic;
        this.content = content;
        this.state = state;
    }

    public void updateState(String state){
        this.state = state;
    }
}