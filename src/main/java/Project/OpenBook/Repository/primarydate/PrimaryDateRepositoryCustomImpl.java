package Project.OpenBook.Repository.primarydate;

import Project.OpenBook.Domain.PrimaryDate;
import Project.OpenBook.Domain.QPrimaryDate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QPrimaryDate.primaryDate;

@Repository
@RequiredArgsConstructor
public class PrimaryDateRepositoryCustomImpl implements PrimaryDateRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public List<PrimaryDate> queryDatesByTopic(Long topicId) {
        return queryFactory.selectFrom(primaryDate)
                .where(primaryDate.topic.id.eq(topicId))
                .fetch();
    }

}
