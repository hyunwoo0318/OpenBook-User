package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PrimaryDate extends BaseEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer extraDate;

    private Boolean extraDateCheck;

    private String extraDateComment;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    public PrimaryDate(Integer extraDate, Boolean extraDateCheck, String extraDateComment, Topic topic) {
        this.extraDate = extraDate;
        this.extraDateCheck = extraDateCheck;
        this.extraDateComment = extraDateComment;
        this.topic = topic;
    }
}
