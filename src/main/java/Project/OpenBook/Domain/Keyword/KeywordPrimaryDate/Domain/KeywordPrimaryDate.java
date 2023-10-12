package Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordPrimaryDate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer extraDate;

    private String extraDateComment;

    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.REMOVE)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    public KeywordPrimaryDate(Integer extraDate, String extraDateComment, Keyword keyword) {
        this.extraDate = extraDate;
        this.extraDateComment = extraDateComment;
        this.keyword = keyword;
    }
}
