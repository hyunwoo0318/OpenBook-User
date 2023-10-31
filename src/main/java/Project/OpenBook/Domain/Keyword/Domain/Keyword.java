package Project.OpenBook.Domain.Keyword.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeyword;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;
import Project.OpenBook.Domain.KeywordAssociation.KeywordAssociation;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String comment;

    private String imageUrl;

    private String dateComment;

    private Integer number = 0;

    private Integer usageCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @OneToMany(mappedBy = "keyword2", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<KeywordAssociation> keywordAssociationList = new ArrayList<>();

    @OneToMany(mappedBy = "keyword",fetch = FetchType.LAZY)
    private List<DescriptionKeyword> descriptionKeywordList = new ArrayList<>();

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<KeywordPrimaryDate> keywordPrimaryDateList = new ArrayList<>();

    public Keyword(Integer number, String name, String comment,String dateComment, Topic topic, String imageUrl) {
        this.number = number;
        this.name = name;
        this.comment = comment;
        this.dateComment = dateComment;
        this.topic = topic;
        this.imageUrl = imageUrl;
        this.usageCount = 0;
    }

    public void updateNumber(Integer number) {
        this.number = number;
    }

    public void updateCount(Integer usageCount) {
        this.usageCount = usageCount;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public Keyword updateKeyword(Integer number, String name, String comment,String dateComment, String imageUrl) {
        this.number = number;
        this.name = name;
        this.comment = comment;
        this.dateComment = dateComment;
        this.imageUrl = imageUrl;
        return this;
    }
}
