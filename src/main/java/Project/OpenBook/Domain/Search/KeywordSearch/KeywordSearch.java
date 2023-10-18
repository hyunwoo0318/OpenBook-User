package Project.OpenBook.Domain.Search.KeywordSearch;

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
}
