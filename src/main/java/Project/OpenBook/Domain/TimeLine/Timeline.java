package Project.OpenBook.Domain.TimeLine;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Era.Era;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Timeline extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer startDate;

    private Integer endDate;

    private Integer jjhListNumber;

    @ManyToOne
    @JoinColumn(name = "era_id")
    private Era era;

    public Timeline(Integer startDate, Integer endDate, Era era) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.era = era;
    }

    public void updateTimeline(Integer startDate, Integer endDate, Era era) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.era = era;
    }

    public void updateJJHListNumber(Integer jjhListNumber) {
        this.jjhListNumber = jjhListNumber;
    }


}
