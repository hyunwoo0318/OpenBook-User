package Project.OpenBook.Repository.question;

import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.choice.ChoiceContentIdDto;
import Project.OpenBook.Dto.description.DescriptionContentIdDto;
import Project.OpenBook.Dto.question.QuestionDto;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QPrimaryDate.primaryDate;
import static Project.OpenBook.Domain.QQuestionChoice.questionChoice;
import static Project.OpenBook.Domain.QQuestionDescription.questionDescription;
import static Project.OpenBook.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class QuestionRepositoryCustomImpl implements QuestionRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public QuestionDto findQuestionById(Long id) {
        Question question = queryFactory.selectFrom(QQuestion.question)
                .where(QQuestion.question.id.eq(id))
                .fetchOne();

        List<Tuple> choices = queryFactory.select( choice.content, choice.id)
                .from(questionChoice)
                .join(questionChoice.choice, choice)
                .where(questionChoice.question.id.eq(id))
                .fetch();

        Tuple description = queryFactory.select(QDescription.description.id, QDescription.description.content)
                .from(questionDescription)
                .join(questionDescription.description, QDescription.description)
                .where(questionDescription.question.id.eq(id))
                .fetchOne();

        List<ChoiceContentIdDto> choiceList = choices.stream().map(c -> new ChoiceContentIdDto(c.get(0, String.class), c.get(1, Long.class))).collect(Collectors.toList());
        DescriptionContentIdDto descriptionDto = new DescriptionContentIdDto(description.get(0, Long.class), description.get(1, String.class));

        QuestionDto questionDto = new QuestionDto(question, choiceList, descriptionDto);
        return questionDto;
    }


}
