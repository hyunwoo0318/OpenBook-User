package Project.OpenBook.Domain.Customer.Domain;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgress;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgress;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Domain.TimelineLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
public class Customer extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private String code;

//    @Column(nullable = false, unique = true)
    private String nickName;

    private Integer solvedNum;

    private Integer age;

    private Integer expertise;

    private String roles;

    private String provider;
    private String password;

    @Column(name = "oAuth_id", length = 1000)
    private String oAuthId;

    private boolean isNew;

    private boolean isSubscribed;

    private boolean isValidated = false;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<JJHListProgress> jjhListProgressList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<JJHContentProgress> jjhContentProgressList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<KeywordLearningRecord> keywordLearningRecordList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<TopicLearningRecord> topicLearningRecordList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<QuestionCategoryLearningRecord> questionCategoryLearningRecordList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<TimelineLearningRecord> timelineLearningRecordList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<ExamQuestionLearningRecord> examQuestionLearningRecordList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE,fetch = FetchType.LAZY)
    private List<RoundLearningRecord> roundLearningRecordList = new ArrayList<>();

    @Builder
    public Customer(String nickName, Integer age, Integer expertise, String roles, String provider, String oAuthId) {
        this.nickName = nickName;
        this.solvedNum = 0;
        this.age = age;
        this.expertise = expertise;
        this.roles = roles;
        this.provider = provider;
        this.oAuthId = oAuthId;
        this.code = UUID.randomUUID().toString().substring(0,16);
        this.isSubscribed = true;
        this.isValidated = true;
    }

    public Customer(String nickName) {
        this.nickName = nickName;
        this.isValidated = false;
    }

    public Customer(String nickName,String password, String roles){
        this.nickName = nickName;
        this.password = password;
        this.roles = roles;
    }

    public Customer updateIsNew(Boolean isNew) {
        this.isNew = isNew;
        return this;
    }



    public void addDetails(String nickName, Integer age, Integer expertise) {
        this.nickName= nickName;
        this.age = age;
        this.expertise = expertise;
        this.isNew = false;
    }


    public Customer setInfo(String nickName, String roles, String provider, String oAuthId) {
        this.nickName = nickName;
        this.solvedNum = 0;
        this.roles = roles;
        this.provider = provider;
        this.oAuthId = oAuthId;
        this.code = UUID.randomUUID().toString().substring(0,16);
        this.isValidated = true;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(roles));
        return authorityList;
    }


    /**
     * UserDetails
     */
    @Override
    public String getUsername() {
        return nickName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
