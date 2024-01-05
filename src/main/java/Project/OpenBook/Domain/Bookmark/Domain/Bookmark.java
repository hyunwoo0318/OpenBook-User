package Project.OpenBook.Domain.Bookmark.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bookmark extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeline_id")
    private Timeline timeline;


    public Bookmark(Customer customer, Topic topic) {
        this.customer = customer;
        this.topic = topic;
    }

    public Bookmark(Customer customer, Timeline timeline) {
        this.customer = customer;
        this.timeline = timeline;
    }
}
