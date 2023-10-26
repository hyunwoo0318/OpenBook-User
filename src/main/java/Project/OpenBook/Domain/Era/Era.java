package Project.OpenBook.Domain.Era;

import Project.OpenBook.Domain.BaseEntity;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Era extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<QuestionCategory> questionCategoryList = new ArrayList<>();

    public Era(String name) {
        this.name = name;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
