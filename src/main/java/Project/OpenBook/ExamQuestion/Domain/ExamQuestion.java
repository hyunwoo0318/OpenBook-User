package Project.OpenBook.ExamQuestion.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Description;
import Project.OpenBook.Round.Domain.Round;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "exam_question")
public class ExamQuestion extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer number;
    private Integer score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    private Round round;

    @OneToMany(mappedBy = "examQuestion")
    private List<Choice> choiceList = new ArrayList<>();

    @OneToOne(mappedBy = "examQuestion")
    private Description description;

    public ExamQuestion(Round round, Integer number, Integer score) {
        this.round = round;
        this.number = number;
        this.score = score;
    }

    public void updateExamQuestion(Integer number, Integer score) {
        this.number = number;
        this.score = score;
    }
}
