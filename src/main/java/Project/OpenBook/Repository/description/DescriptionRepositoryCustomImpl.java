package Project.OpenBook.Repository.description;

import Project.OpenBook.Domain.Description;
import Project.OpenBook.Domain.QChoice;
import Project.OpenBook.Domain.QDescription;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;

@Repository
@RequiredArgsConstructor
public class DescriptionRepositoryCustomImpl implements DescriptionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Description findRandDescriptionByTopic(String topicTitle) {
        return queryFactory.selectFrom(description)
                .where(description.topic.title.eq(topicTitle))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public Description queryRandDescriptionByDescription(Long descriptionId) {
        QDescription des1 = new QDescription("description1");
        return queryFactory.selectFrom(description)
                .innerJoin(des1)
                .where(des1.id.eq(descriptionId))
                .where(des1.topic.eq(description.topic))
                .orderBy(Expressions.numberTemplate(Double.class, "function('rand')").asc())
                .limit(1)
                .fetchOne();
    }

    @Override
    public List<Description> findDescriptionsByTopic(String topicTitle) {
        return queryFactory.selectFrom(description)
                .where(description.topic.title.eq(topicTitle))
                .fetch();
    }
}
