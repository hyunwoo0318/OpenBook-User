package Project.OpenBook.Domain.Category.Domain;

import Project.OpenBook.Domain.BaseEntity;
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
public class Category extends BaseEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    @OneToMany(mappedBy = "category")
    private List<Topic> topicList = new ArrayList<>();

    public Category(String name) {
        this.name = name;
    }

    public void changeName(String name) {
        this.name = name;
    }
}
