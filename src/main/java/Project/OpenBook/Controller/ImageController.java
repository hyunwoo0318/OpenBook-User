package Project.OpenBook.Controller;

import Project.OpenBook.Repository.imagefile.ImageFileRepository;
import Project.OpenBook.Service.ImageFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequiredArgsConstructor
public class ImageController {

    private final ImageFileService imageFileService;

//    @GetMapping("/images/{imageId}")
//    public ResponseEntity queryImage(@PathVariable("imageId") Long imageId) throws IOException {
//        String imageUrl = imageFileService.findImageUrl(imageId);
//    }
}
