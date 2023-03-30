package Project.OpenBook.Domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QChapter is a Querydsl query type for Chapter
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChapter extends EntityPathBase<Chapter> {

    private static final long serialVersionUID = 473527305L;

    public static final QChapter chapter = new QChapter("chapter");

    public final QBaseEntity _super = new QBaseEntity(this);

    //inherited
    public final StringPath createdTime = _super.createdTime;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final StringPath modifiedTime = _super.modifiedTime;

    public final NumberPath<Integer> number = createNumber("number", Integer.class);

    public final StringPath title = createString("title");

    public final ListPath<Topic, QTopic> topicList = this.<Topic, QTopic>createList("topicList", Topic.class, QTopic.class, PathInits.DIRECT2);

    public QChapter(String variable) {
        super(Chapter.class, forVariable(variable));
    }

    public QChapter(Path<? extends Chapter> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChapter(PathMetadata metadata) {
        super(Chapter.class, metadata);
    }

}

