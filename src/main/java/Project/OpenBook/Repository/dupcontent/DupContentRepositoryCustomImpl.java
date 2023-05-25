package Project.OpenBook.Repository.dupcontent;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.DupContent;
import Project.OpenBook.Domain.QChoice;
import Project.OpenBook.Domain.QDupContent;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDupContent.dupContent;

@Repository
@RequiredArgsConstructor
public class DupContentRepositoryCustomImpl implements DupContentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Choice> queryDupContentChoices(Long descriptionId) {
        return queryFactory.selectFrom(choice)
                .where(choice.id.in(
                        JPAExpressions
                                .select(dupContent.id)
                                .from(dupContent)
                                .where(dupContent.description.id.eq(descriptionId))
                        ))
                .fetch();
    }

    @Override
    public DupContent queryDupContent(Long descriptionId, Long choiceId) {
        return queryFactory.selectFrom(dupContent)
                .where(dupContent.description.id.eq(descriptionId))
                .where(dupContent.choice.id.eq(choiceId))
                .fetchOne();
    }
}
