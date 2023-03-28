package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chapter extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int num;

    @Builder
    public Chapter(String title, int num) {
        this.title = title;
        this.num = num;
    }

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL)
    private List<Theme> themeList = new ArrayList<>();


}
