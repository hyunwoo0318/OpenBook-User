package Project.OpenBook.Domain;

import lombok.Getter;


@Getter
public class ImageFile {

    private Long imageId;

    private Long keywordId;

    private String storedFileName;

    public ImageFile(Long keywordId, String storedFileName) {
        this.keywordId = keywordId;
        this.storedFileName = storedFileName;
    }

    public void setId(Long id){
        this.imageId = id;
    }

    public void setKeywordId(Long keywordId) {
        this.keywordId = keywordId;
    }
}
