package Project.OpenBook.Domain.Search.KeywordSearch;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;

@Document(indexName = "keywords")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class KeywordSearch {

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String comment;

    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Integer)
    private Integer chapterNumber;
    @Field(type = FieldType.Text)
    private String chapterTitle;
    @Field(type = FieldType.Text)
    private String topicTitle;

    public KeywordSearch(Keyword keyword) {
        this.id = keyword.getId();
        this.name = keyword.getName();
        this.comment = keyword.getComment();
        Topic topic = keyword.getTopic();
        this.topicTitle = topic.getTitle();
        Chapter chapter = topic.getChapter();
        this.chapterNumber= chapter.getNumber();
        this.chapterTitle = chapter.getTitle();
    }
}
