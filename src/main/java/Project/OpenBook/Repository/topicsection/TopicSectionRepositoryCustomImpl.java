package Project.OpenBook.Repository.topicsection;

import Project.OpenBook.Domain.QTopicSection;
import Project.OpenBook.Domain.TopicSection;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.QTopicSection.topicSection;

@RequiredArgsConstructor
public class TopicSectionRepositoryCustomImpl implements TopicSectionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<TopicSection> queryTopicSection(Long customerId, String topicTitle, String content) {
        TopicSection findTopicSection = queryFactory.selectFrom(topicSection)
                .where(topicSection.customer.id.eq(customerId))
                .where(topicSection.topic.title.eq(topicTitle))
                .where(topicSection.content.eq(content))
                .fetchOne();
        return Optional.ofNullable(findTopicSection);
    }

    @Override
    public List<TopicSection> queryTopicSections(Long customerId, Integer chapterNum) {
        return queryFactory.selectFrom(topicSection)
                .where(topicSection.customer.id.eq(customerId))
                .where(topicSection.topic.chapter.number.eq(chapterNum))
                .fetch();
    }
}
