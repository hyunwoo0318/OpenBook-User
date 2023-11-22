package Project.OpenBook.Domain.Timeline.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Era.Era;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timeline extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer startDate;

    private Integer endDate;

    private Integer count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "era_id")
    private Era era;

    @OneToMany(mappedBy = "timeline",cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<JJHList> jjhLists = new ArrayList<>();



//    @OneToMany(mappedBy = "timeline",fetch = FetchType.LAZY)
//    private List<QuestionCategory> questionCategoryList = new ArrayList<>();

    public Timeline(String title, Integer startDate, Integer endDate, Era era) {
        this.count = 0;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.era = era;
    }

    public void updateTimeline(String title, Integer startDate, Integer endDate, Era era) {
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.era = era;
    }

    public void updateCount() {
        this.count++;
    }



}
