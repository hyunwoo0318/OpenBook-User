package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Question extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String prompt;

    @Column(nullable = false)
    private Long answerChoiceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private Long type;

    @Builder
    public Question(String prompt, Long answerChoiceId, Long type, Category category) {
        this.prompt = prompt;
        this.answerChoiceId = answerChoiceId;
        this.type = type;
        this.category = category;
    }

    public Question updateQuestion(String prompt, Long answerChoiceId, Long type, Category category){
        this.prompt = prompt;
        this.answerChoiceId = answerChoiceId;
        this.type = type;
        this.category = category;
        return this;
    }
}
