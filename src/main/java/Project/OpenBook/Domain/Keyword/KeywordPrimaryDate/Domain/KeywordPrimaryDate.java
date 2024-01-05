package Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordPrimaryDate extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer extraDate;

    private String extraDateComment;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    public KeywordPrimaryDate(Integer extraDate, String extraDateComment, Keyword keyword) {
        this.extraDate = extraDate;
        this.extraDateComment = extraDateComment;
        this.keyword = keyword;
    }
}
