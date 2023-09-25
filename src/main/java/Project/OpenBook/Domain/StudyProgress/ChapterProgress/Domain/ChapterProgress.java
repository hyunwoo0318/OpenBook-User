package Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chapter_progress")
public class ChapterProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    private LocalDateTime lastStudyTime;

    private Integer wrongCount=0;

    private String progress;

    public ChapterProgress(Customer customer, Chapter chapter) {
        this.customer = customer;
        this.chapter = chapter;
    }

    public ChapterProgress(Customer customer, Chapter chapter, Integer wrongCount, String progress) {
        this.customer = customer;
        this.chapter = chapter;
        this.wrongCount = wrongCount;
        this.progress = progress;
    }

    public ChapterProgress updateWrongCount(int wrongCount) {
        this.wrongCount += wrongCount;
        return this;
    }

    public ChapterProgress updateProgress(String progress) {
        this.progress = progress;
        return this;
    }
}