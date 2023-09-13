package Project.OpenBook.Domain.Round.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "round")
    private List<ExamQuestion> examQuestionList;

    public Round(Integer date, Integer number) {
        this.date = date;
        this.number = number;
    }

    public Round updateRound(Integer date, Integer number) {
        this.date = date;
        this.number = number;
        return this;
    }
}
