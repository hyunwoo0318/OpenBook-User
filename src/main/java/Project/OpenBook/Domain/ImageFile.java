package Project.OpenBook.Domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageFile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    private String originalName;

    private String storedFileName;

    public ImageFile(String originalName ,String storedFileName,Keyword keyword) {
        this.storedFileName = storedFileName;
        this.originalName = originalName;
        this.keyword = keyword;
    }

}
