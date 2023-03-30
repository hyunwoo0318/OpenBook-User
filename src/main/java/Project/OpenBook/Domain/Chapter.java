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

    @Column(nullable = false, unique = true)
    private int number;

    @Builder
    public Chapter(String title, int number) {
        this.title = title;
        this.number = number;
    }

    @Column(nullable = false)
    private String title;

    @OneToMany(mappedBy = "chapter")
    private List<Topic> topicList = new ArrayList<>();

    public Chapter updateChapter(String title, int num) {
        this.title = title;
        this.number = num;
        return this;
    }

}
