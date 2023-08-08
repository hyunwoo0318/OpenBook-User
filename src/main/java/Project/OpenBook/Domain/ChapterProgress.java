package Project.OpenBook.Domain;

import Project.OpenBook.Constants.ProgressConst;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chapter_progress")
public class ChapterProgress  extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    private String progress= ProgressConst.NOT_STARTED;

    private LocalDateTime lastStudyTime;

    private Integer wrongCount=0;

    public ChapterProgress(Customer customer, Chapter chapter) {
        this.customer = customer;
        this.chapter = chapter;
    }

    public ChapterProgress updateLastStudyTime() {
        this.lastStudyTime = LocalDateTime.now();
        return this;
    }

    public ChapterProgress updateWrongCount(int wrongCount) {
        this.wrongCount += wrongCount;
        return this;
    }
}
