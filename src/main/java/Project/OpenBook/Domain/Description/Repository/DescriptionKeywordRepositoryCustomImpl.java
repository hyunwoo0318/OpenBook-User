package Project.OpenBook.Domain.Description.Repository;

import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Description.Service.DescriptionKeyword;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Description.Domain.QDescription.description;
import static Project.OpenBook.Domain.Description.Service.QDescriptionKeyword.descriptionKeyword;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class DescriptionKeywordRepositoryCustomImpl implements DescriptionKeywordRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public List<DescriptionKeyword> queryDescriptionKeywords(Description description) {
        return queryFactory.selectFrom(descriptionKeyword)
                .leftJoin(descriptionKeyword.keyword, keyword).fetchJoin()
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .where(descriptionKeyword.description.eq(description))
                .fetch();
    }

    @Override
    public Optional<Description> queryDescription(Integer roundNumber, Integer questionNumber) {
        Description findDescription = queryFactory.selectFrom(description)
                .leftJoin(description.descriptionKeywordList, descriptionKeyword).fetchJoin()
                .leftJoin(descriptionKeyword.keyword).fetchJoin()
                .leftJoin(keyword.topic, topic).fetchJoin()
                .leftJoin(topic.chapter, chapter).fetchJoin()
                .where(description.examQuestion.round.number.eq(roundNumber))
                .where(description.examQuestion.number.eq(questionNumber))
                .fetchOne();
        return Optional.ofNullable(findDescription);

    }

}
