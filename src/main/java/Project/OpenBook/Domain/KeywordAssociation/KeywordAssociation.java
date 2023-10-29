package Project.OpenBook.Domain.KeywordAssociation;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordAssociation {

    @Id @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_keyword_id")
    private Keyword keyword2;

    private Integer val = 0;

    public KeywordAssociation(Keyword keyword1, Keyword keyword2) {
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        val = 0;
    }

    public KeywordAssociation(Keyword keyword1, Keyword keyword2, Integer val) {
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.val = val;
    }

    public KeywordAssociation updateVal(Integer val) {
        this.val += val;
        return this;
    }
}
