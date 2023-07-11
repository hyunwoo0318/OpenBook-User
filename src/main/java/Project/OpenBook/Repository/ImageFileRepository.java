package Project.OpenBook.Repository;

import Project.OpenBook.Domain.ImageFile;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class ImageFileRepository {

    private final Map<Long, ImageFile> m = new HashMap<>();

    private static Long sequence = 0L;

    public void save(ImageFile imageFile) {
        sequence++;
        imageFile.setId(sequence);
        m.put(sequence, imageFile);
    }

    public void delete(Long imageFileId) {
        m.remove(imageFileId);
    }

    public ImageFile findByImageId(Long imageId) {
       return m.get(imageId);
    }

    public ImageFile findByKeywordId(Long keywordId) {
        return null;
    }
}
