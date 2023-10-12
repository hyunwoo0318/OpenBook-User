package Project.OpenBook.Domain.ExamQuestion.Domain;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Round.Domain.Round;
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
    private Integer answer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id")
    private Round round;

    @OneToMany(mappedBy = "examQuestion",fetch = FetchType.LAZY)
    private List<Choice> choiceList = new ArrayList<>();

    @OneToOne(mappedBy = "examQuestion")
    private Description description;

    @Enumerated(EnumType.STRING)
    private ChoiceType choiceType;

    public ExamQuestion(Round round, Integer number, Integer score,Integer answer, ChoiceType choiceType) {
        this.round = round;
        this.number = number;
        this.score = score;
        this.answer = answer;
        this.choiceType = choiceType;
    }

    public ExamQuestion updateExamQuestion(Integer number, Integer score,Integer answer, ChoiceType choiceType) {
        this.number = number;
        this.score = score;
        this.answer = answer;
        this.choiceType = choiceType;
        return this;
    }
}
