package Project.OpenBook.Domain;

import Project.OpenBook.Constants.Role;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.dynamic.loading.InjectionClassLoader;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity implements OAuth2User {

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

    private String role;

    private String provider;

    @Column(name = "oAuth_id", length = 1000)
    private String oAuthId;

    @Column(name = "is_new", columnDefinition = "TINYINT(1)")
    private boolean isNew = true;

    @Column(name = "is_subscribed", columnDefinition = "TINYINT(1)")
    private boolean isSubscribed = true;

    @Builder
    public Customer(String nickName,Integer age, Integer expertise, String role, String provider, String oAuthId, Boolean isSubscribed) {
        this.nickName = nickName;
        this.solvedNum = 0;
        this.age = age;
        this.expertise = expertise;
        this.role = role;
        this.provider = provider;
        this.oAuthId = oAuthId;
        this.code = UUID.randomUUID().toString().substring(0,16);
        this.isSubscribed = true;
    }

    public void addDetails(String nickName, Integer age, Integer expertise) {
        this.nickName= nickName;
        this.age = age;
        this.expertise = expertise;
        this.isNew = false;
    }


    @Override
    public Map<String, Object> getAttributes() {
        Map<String, Object> m = new HashMap<>();
        m.put("role", this.role);
        m.put("nickName", this.nickName);
        m.put("isNew", this.isNew);
        return m;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(role));
        return authorityList;
    }

    @Override
    public <A> A getAttribute(String name) {
        return OAuth2User.super.getAttribute(name);
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}
