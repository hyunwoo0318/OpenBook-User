package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.ImageFile;
import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Repository.imagefile.ImageFileRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageFileService {

    private final ImageFileRepository imageFileRepository;
    @Value("${file.dir}/")
    private String fileDirPath;
    public ImageFile storeFile(String encodedFile, Keyword keyword) throws IOException {

        String[] parts = encodedFile.split(",");
        String header = parts[0];
        String encodedImage = parts[1]; // "data:image/png;base64," 부분을 제외한 이미지 인코딩 값

        String ext = extractExtension(header);
        // 이미지 디코드
        byte[] decodedBytes = Base64.getDecoder().decode(encodedImage);

        String storedFileName = createStoredFileName(ext);

        // 이미지 파일 저장
        Path filePath = Files.createFile(Path.of(fileDirPath, storedFileName));
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(decodedBytes);
        }

        ImageFile imageFile = new ImageFile(storedFileName, keyword);

        imageFileRepository.save(imageFile);
        return imageFile;
    }

    private String extractExtension(String header) {
        String[] parts = header.split("/");
        String mediaType = parts[1];
        String[] subParts = mediaType.split(";");
        return subParts[0];
    }

    public boolean deleteImages(Long keywordId) {
        List<ImageFile> imageFileList = imageFileRepository.queryByKeyword(keywordId);
        for (ImageFile imageFile : imageFileList) {
            String storedFileName = imageFile.getStoredFileName();
            String path = createPath(storedFileName);
            File file = new File(path);
            boolean ret = file.delete();
            if (ret) {
                imageFileRepository.delete(imageFile);
            }else{
                return false;
            }
        }
        return true;
    }

    //storedFileName 설정하는 메서드
    private String createStoredFileName(String ext) {
        String uuid = UUID.randomUUID().toString();
        return uuid +"."+ ext;
    }

    //이미지를 저장할 파일 경로 설정하는 메서드
    private String createPath(String storedFileName) {
        return fileDirPath + storedFileName;
    }

    //이미지가 저장된 경로 찾는 메서드
    public String findPath(Long imgId) {
        ImageFile imageFile = imageFileRepository.findById(imgId).orElseThrow(() -> {
            throw new CustomException(ErrorCode.SENTENCE_NOT_FOUND);
        });

        String storedFileName = imageFile.getStoredFileName();
        return createPath(storedFileName);
    }


}
