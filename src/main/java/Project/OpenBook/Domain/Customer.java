package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nickName;

    private Integer solvedNum;

    private Integer age;

    private Integer currentGrade;

    private String role;

    @Builder
    public Customer(String nickName,Integer age, Integer currentGrade, String role) {
        this.nickName = nickName;
        this.solvedNum = 0;
        this.age = age;
        this.currentGrade = currentGrade;
        this.role = role;
    }


}
