package Project.OpenBook.Domain.QuestionCategory.Repo;

import static Project.OpenBook.Domain.QuestionCategory.Domain.QQuestionCategory.questionCategory;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class QuestionCategoryRepositoryCustomImpl implements QuestionCategoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;


    @Override
    public Optional<QuestionCategory> queryQuestionCategoriesWithTopicList(Long id) {
        QuestionCategory findQuestionCategory = queryFactory.selectFrom(questionCategory).distinct()
            .leftJoin(questionCategory.topicList).fetchJoin()
            .where(questionCategory.id.eq(id))
            .fetchOne();
        return Optional.ofNullable(findQuestionCategory);
    }
}
