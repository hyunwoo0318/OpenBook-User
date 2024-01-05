package Project.OpenBook.Domain.Keyword.Domain;

import Project.OpenBook.Constants.KeywordUsageConst;
import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeyword;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Keyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String comment;

    private String imageUrl;

    private String dateComment;

    private Integer number = 0;

    private Integer questionProb;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;


    @OneToMany(mappedBy = "keyword", fetch = FetchType.LAZY)
    private List<DescriptionKeyword> descriptionKeywordList = new ArrayList<>();

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<KeywordPrimaryDate> keywordPrimaryDateList = new ArrayList<>();

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<KeywordLearningRecord> keywordLearningRecordList = new ArrayList<>();

    public Keyword(Integer number, String name, String comment, String dateComment, Topic topic,
        String imageUrl) {
        this.number = number;
        this.name = name;
        this.comment = comment;
        this.dateComment = dateComment;
        this.topic = topic;
        this.imageUrl = imageUrl;
        this.questionProb = KeywordUsageConst.KEYWORD_USAGE_DEFAULT;
    }

    public Keyword(String name, String comment, Topic topic) {
        this.topic = topic;
        this.name = name;
        this.comment = comment;
    }

    public void updateCount(Integer questionProb) {
        this.questionProb = questionProb;
    }
}
