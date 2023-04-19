package Project.OpenBook.Repository.choice;

import Project.OpenBook.Domain.Choice;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QChoice.choice;

@Repository
@RequiredArgsConstructor
public class ChoiceRepositoryCustomImpl implements ChoiceRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Choice> queryChoicesById(List<Long> choiceIdList) {
        return queryFactory.selectFrom(choice)
                .where(choice.id.in(choiceIdList))
                .fetch();
    }

    @Override
    public List<Choice> queryChoiceByTopicTitle(String topicTitle) {
        return queryFactory.selectFrom(choice)
                .where(choice.topic.title.eq(topicTitle))
                .fetch();
    }
}
