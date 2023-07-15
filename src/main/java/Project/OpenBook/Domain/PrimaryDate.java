package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrimaryDate extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer date;

    private Boolean dateCheck;

    private String dateComment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public PrimaryDate(Integer date, Boolean dateCheck, String dateComment, Topic topic) {
        this.date = date;
        this.dateCheck = dateCheck;
        this.dateComment = dateComment;
        this.topic = topic;
    }
}
