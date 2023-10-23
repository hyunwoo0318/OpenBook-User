package Project.OpenBook.Domain.Search.ChapterSearch;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Column;
import javax.persistence.Id;

@Document(indexName = "chapters")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ChapterSearch {
    @Column(unique = true)
    @Field(type = FieldType.Text)
    private String title;

    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Integer)
    private Integer chapterNumber;

    public ChapterSearch(Chapter chapter) {
        this.id = chapter.getId();
        this.title = chapter.getTitle();
        this.chapterNumber = chapter.getNumber();
    }
}
