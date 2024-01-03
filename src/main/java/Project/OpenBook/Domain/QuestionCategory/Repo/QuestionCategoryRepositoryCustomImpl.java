package Project.OpenBook.Domain.QuestionCategory.Repo;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static Project.OpenBook.Domain.QuestionCategory.Domain.QQuestionCategory.questionCategory;

@Repository
@RequiredArgsConstructor
public class QuestionCategoryRepositoryCustomImpl implements QuestionCategoryRepositoryCustom{

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
