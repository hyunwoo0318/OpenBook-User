package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "BINARY(16)")
    private String code;

    @Column(nullable = false, unique = true)
    private String nickName;

    private Integer solvedNum;

    private Integer age;

    private Integer expertise;

    private String roles;

    private String provider;
    private String password;

    @Column(name = "oAuth_id", length = 1000)
    private String oAuthId;

    @Column(name = "is_new", columnDefinition = "TINYINT(1)")
    private boolean isNew = true;

    @Column(name = "is_subscribed", columnDefinition = "TINYINT(1)")
    private boolean isSubscribed = true;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private List<ChapterSection> chapterSectionList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private List<ChapterProgress> chapterProgressList = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.REMOVE)
    private List<TopicProgress> topicProgressList = new ArrayList<>();

    @Builder
    public Customer(String nickName, Integer age, Integer expertise, String roles, String provider, String oAuthId, Boolean isSubscribed) {
        this.nickName = nickName;
        this.solvedNum = 0;
        this.age = age;
        this.expertise = expertise;
        this.roles = roles;
        this.provider = provider;
        this.oAuthId = oAuthId;
        this.code = UUID.randomUUID().toString().substring(0,16);
        this.isSubscribed = true;
    }

    public Customer(String nickName,String password, String roles){
        this.nickName = nickName;
        this.password = password;
        this.roles = roles;
    }



    public void addDetails(String nickName, Integer age, Integer expertise) {
        this.nickName= nickName;
        this.age = age;
        this.expertise = expertise;
        this.isNew = false;
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
