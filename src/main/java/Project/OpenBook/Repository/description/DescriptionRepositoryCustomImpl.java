package Project.OpenBook.Repository.description;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Description;
import Project.OpenBook.Domain.QChoice;
import Project.OpenBook.Domain.QDescription;
import Project.OpenBook.Dto.choice.DupChoiceDto;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.MathExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.Domain.QDupContent.dupContent;

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
        return queryFactory.select(description)
                .from(description, des1)
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

    @Override
    public List<DupChoiceDto> queryDupChoices(Long descriptionId, String topicTitle) {
        List<Choice> topicChoiceList = queryFactory.select(choice)
                .from(choice)
                .where(choice.topic.title.eq(topicTitle))
                .fetch();
        Set<Long> dupChoiceIdSet = queryFactory.select(dupContent.choice.id)
                .from(dupContent)
                .where(dupContent.description.id.eq(descriptionId))
                .fetch().stream().collect(Collectors.toSet());

        List<DupChoiceDto> dupChoiceDtoList = new ArrayList<>();

        for (Choice ch1 : topicChoiceList) {
            //보기와 내용이 중복되었다고 선정된 경우
            if(dupChoiceIdSet.contains(ch1.getId())){
                dupChoiceDtoList.add(new DupChoiceDto(ch1.getContent(), ch1.getId(), true));
            }else{
                //보기와 내용이 중복되었다고 선정되지 않은 경우
                dupChoiceDtoList.add(new DupChoiceDto(ch1.getContent(), ch1.getId(), false));
            }
        }
        return dupChoiceDtoList;
    }

    @Override
    public Description queryDescriptionByContent(String content) {
        return queryFactory.selectFrom(description)
                .where(description.content.eq(content))
                .fetchOne();
    }
}
