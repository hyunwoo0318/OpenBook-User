package Project.OpenBook.Domain;

import Project.OpenBook.Chapter.Domain.Chapter;
import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.StateConst;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "chapter_section")
public class ChapterSection extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    private String content = ContentConst.NOT_STARTED.getName();

    private String state = StateConst.LOCKED.getName();

    private LocalDateTime lastStudyTime;

    public ChapterSection(Customer customer, Chapter chapter) {
        this.customer = customer;
        this.chapter = chapter;
    }

    public ChapterSection(Customer customer, Chapter chapter, String content, String state){
        this.customer = customer;
        this.chapter = chapter;
        this.content = content;
        this.state = state;
    }

    public ChapterSection updateState(String stateConst) {
        this.state = stateConst;
        this.lastStudyTime = LocalDateTime.now();
        return this;
    }


}
