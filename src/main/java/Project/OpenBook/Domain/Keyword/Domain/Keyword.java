package Project.OpenBook.Domain.Keyword.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.Description.Service.DescriptionKeyword;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.KeywordPrimaryDate;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @OneToMany(mappedBy = "keyword")
    private List<DescriptionKeyword> descriptionKeywordList = new ArrayList<>();

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.REMOVE)
    private List<KeywordPrimaryDate> keywordPrimaryDateList = new ArrayList<>();

    public Keyword(String name, String comment,String dateComment, Topic topic, String imageUrl) {
        this.name = name;
        this.comment = comment;
        this.dateComment = dateComment;
        this.topic = topic;
        this.imageUrl = imageUrl;
    }

    public void changeName(String name) {
        this.name = name;
    }

    public Keyword updateKeyword(String name, String comment,String dateComment, String imageUrl) {
        this.name = name;
        this.comment = comment;
        this.dateComment = dateComment;
        this.imageUrl = imageUrl;
        return this;
    }
}
