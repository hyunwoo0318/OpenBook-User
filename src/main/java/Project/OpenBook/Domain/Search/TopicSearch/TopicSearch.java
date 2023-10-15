package Project.OpenBook.Domain.Search.TopicSearch;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Lob;

@Document(indexName = "topics")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class TopicSearch {

    @Lob
    @Field(type = FieldType.Text)
    private String detail;

    @Column(unique = true)
    @Field(type = FieldType.Text)
    private String title;

    @Id
    @Field(type = FieldType.Long)
    private Long id;
}
