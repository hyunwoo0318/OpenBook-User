package Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "description_keyword")
public class DescriptionKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "description_id")
    private Description description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    public DescriptionKeyword(Description description, Keyword keyword) {
        this.description = description;
        this.keyword = keyword;
    }
}
