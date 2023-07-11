package Project.OpenBook.Service;

import Project.OpenBook.Domain.ImageFile;
import Project.OpenBook.Repository.ImageFileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ImageFileService {

    private final ImageFileRepository imageFileRepository;

    public ImageFile convertToImageFile(MultipartFile file) {
        return null;
    }

    public MultipartFile convertToMultiPartFIle(ImageFile imageFile) {

         return null;
    }
}
