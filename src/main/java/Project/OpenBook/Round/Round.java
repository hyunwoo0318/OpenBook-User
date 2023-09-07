package Project.OpenBook.Round;

import Project.OpenBook.Domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Round extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer date;

    @Column(nullable = false, unique = true)
    private Integer number;

    public Round(Integer date, Integer number) {
        this.date = date;
        this.number = number;
    }

    public void updateRound(Integer date, Integer number) {
        this.date = date;
        this.number = number;
    }
}
