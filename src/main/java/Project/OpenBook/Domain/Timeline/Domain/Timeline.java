package Project.OpenBook.Domain.Timeline.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Era.Era;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContent;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Domain.TimelineLearningRecord;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timeline extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private Integer startDate;

    private Integer endDate;

    private Integer count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "era_id")
    private Era era;

    @OneToMany(mappedBy = "timeline", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<JJHList> jjhLists = new ArrayList<>();

    @OneToMany(mappedBy = "timeline", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<TimelineLearningRecord> timelineLearningRecordList = new ArrayList<>();

    @OneToMany(mappedBy = "timeline", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<JJHContent> jjhContentList = new ArrayList<>();


    public Timeline(String title, Integer startDate, Integer endDate, Era era) {
        this.count = 0;
        this.title = title;
        this.startDate = startDate;
        this.endDate = endDate;
        this.era = era;
    }


    public void updateCount() {
        this.count++;
    }


}
