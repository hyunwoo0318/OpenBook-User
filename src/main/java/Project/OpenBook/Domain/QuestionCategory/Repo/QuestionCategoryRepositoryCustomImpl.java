package Project.OpenBook.Domain.QuestionCategory.Repo;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Category.Domain.QCategory.category;
import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.QuestionCategory.Domain.QQuestionCategory.questionCategory;

@Repository
@RequiredArgsConstructor
public class QuestionCategoryRepositoryCustomImpl implements QuestionCategoryRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<QuestionCategory> queryQuestionCategoriesForAdmin() {
        return queryFactory.selectFrom(questionCategory).distinct()
                .leftJoin(questionCategory.category, category).fetchJoin()
                .leftJoin(questionCategory.era, era).fetchJoin()
                .leftJoin(questionCategory.topicList).fetchJoin()
                .fetch();
    }

    @Override
    public Optional<QuestionCategory> queryQuestionCategoriesWithTopicList(Long id) {
        QuestionCategory findQuestionCategory = queryFactory.selectFrom(questionCategory).distinct()
                .leftJoin(questionCategory.topicList).fetchJoin()
                .where(questionCategory.id.eq(id))
                .fetchOne();
        return Optional.ofNullable(findQuestionCategory);
    }
}
