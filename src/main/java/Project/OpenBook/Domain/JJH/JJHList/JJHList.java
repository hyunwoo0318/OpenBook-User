package Project.OpenBook.Domain.JJH.JJHList;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContent;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgress;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "jjh_list")
public class JJHList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeline_id")
    private Timeline timeline;

    @OneToMany(mappedBy = "jjhList", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<JJHContent> jjhContentList = new ArrayList<>();

    @OneToMany(mappedBy = "jjhList",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<JJHListProgress> jjhListProgressList = new ArrayList<>();


    public JJHList(Integer number, Chapter chapter) {
        this.number = number;
        this.chapter = chapter;
    }

    public JJHList(Integer number, Timeline timeline) {
        this.number = number;
        this.timeline = timeline;
    }

    public void updateNumber(Integer number) {
        this.number = number;
    }


}
