package Project.OpenBook.Repository.imagefile;

import Project.OpenBook.Domain.ImageFile;
import Project.OpenBook.Domain.QImageFile;
import com.querydsl.core.QueryFactory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static Project.OpenBook.Domain.QImageFile.*;

@Repository
@RequiredArgsConstructor
public class ImageFileRepositoryCustomImpl implements ImageFileRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    @Override
    public List<ImageFile> queryByKeyword(Long keywordId) {
        return queryFactory.selectFrom(imageFile)
                .where(imageFile.keyword.id.eq(keywordId))
                .fetch();
    }
}
