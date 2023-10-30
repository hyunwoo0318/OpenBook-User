package Project.OpenBook.Domain.JJH.JJHContentProgress;

import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.JJH.dto.TotalProgressDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Category.Domain.QCategory.category;
import static Project.OpenBook.Domain.Chapter.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.Era.QEra.era;
import static Project.OpenBook.Domain.JJH.JJHContent.QJJHContent.jJHContent;
import static Project.OpenBook.Domain.JJH.JJHContentProgress.QJJHContentProgress.jJHContentProgress;
import static Project.OpenBook.Domain.JJH.JJHList.QJJHList.jJHList;
import static Project.OpenBook.Domain.QuestionCategory.Domain.QQuestionCategory.questionCategory;
import static Project.OpenBook.Domain.Timeline.Domain.QTimeline.timeline;
import static Project.OpenBook.Domain.Topic.Domain.QTopic.topic;

@Repository
@RequiredArgsConstructor
public class JJHContentProgressRepositoryCustomImpl implements JJHContentProgressRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<JJHContentProgress> queryJJHContentProgressForCustomer(Customer customer, Integer number) {
        return queryFactory.selectFrom(jJHContentProgress)
                .leftJoin(jJHContentProgress.jjhContent, jJHContent).fetchJoin()
                .leftJoin(jJHContent.jjhList, jJHList).fetchJoin()
                .leftJoin(jJHContent.topic, topic).fetchJoin()
                .leftJoin(topic.questionCategory, questionCategory).fetchJoin()
                .leftJoin(questionCategory.category, category).fetchJoin()
                .leftJoin(jJHContent.chapter, chapter).fetchJoin()
                .leftJoin(jJHContent.timeline, timeline).fetchJoin()
                .leftJoin(timeline.era, era).fetchJoin()
                .where(jJHContentProgress.customer.eq(customer))
                .where(jJHContent.jjhList.number.eq(number))
                .fetch();
    }

    @Override
    public Optional<JJHContentProgress> queryJJHContentProgressWithJJHContent(Customer customer,Integer number) {
        JJHContentProgress findProgress = queryFactory.selectFrom(jJHContentProgress)
                .leftJoin(jJHContentProgress.jjhContent, jJHContent).fetchJoin()
                .leftJoin(jJHContent.jjhList, jJHList).fetchJoin()
                .where(jJHContentProgress.jjhContent.number.eq(number))
                .where(jJHContentProgress.customer.eq(customer))
                .fetchOne();
        return Optional.ofNullable(findProgress);
    }

    @Override
    public TotalProgressDto queryTotalProgressDto(Customer customer) {
        long totalCount = queryFactory.selectFrom(jJHContentProgress)
                .stream().count();
        long openCount = queryFactory.selectFrom(jJHContentProgress)
                .where(jJHContentProgress.state.ne(StateConst.LOCKED))
                .stream().count();
        return new TotalProgressDto((openCount * 100) / totalCount);
    }
}
