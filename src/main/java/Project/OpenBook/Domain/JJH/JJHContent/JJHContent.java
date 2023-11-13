package Project.OpenBook.Domain.JJH.JJHContent;

import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgress;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "jjh_content")
public class JJHContent {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ContentConst content;

    private Integer number;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jjh_list_id")
    public JJHList jjhList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chapter_id")
    public Chapter chapter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    public Topic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "timeline_id")
    public Timeline timeline;

    @OneToMany(mappedBy = "jjhContent",fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<JJHContentProgress> jjhContentProgressList = new ArrayList<>();

    public JJHContent(ContentConst content, Integer number, JJHList jjhList, Chapter chapter) {
        this.content = content;
        this.number = number;
        this.jjhList = jjhList;
        this.chapter = chapter;
    }

    public JJHContent(ContentConst content, Integer number, JJHList jjhList, Topic topic) {
        this.content = content;
        this.number = number;
        this.jjhList = jjhList;
        this.topic = topic;
    }

    public JJHContent(ContentConst content, Integer number, JJHList jjhList, Timeline timeline) {
        this.content = content;
        this.number = number;
        this.jjhList = jjhList;
        this.timeline = timeline;
    }

    public void updateNumber(Integer number) {
        this.number = number;
    }
}
