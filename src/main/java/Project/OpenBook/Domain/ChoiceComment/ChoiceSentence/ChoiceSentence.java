package Project.OpenBook.Domain.ChoiceComment.ChoiceSentence;

import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "choice_sentence")
public class ChoiceSentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "choice_id")
    private Choice choice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sentence_id")
    private Sentence sentence;

    public ChoiceSentence(Choice choice, Sentence sentence) {
        this.choice = choice;
        this.sentence = sentence;
    }
}