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
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageFileService {

    private final ImageFileRepository imageFileRepository;
    @Value("${file.dir}/")
    private String fileDirPath;
    public ImageFile storeFile(MultipartFile file, Keyword keyword) throws IOException {

        String originalName = file.getOriginalFilename();
        String storedFileName = createStoredFileName(originalName);
        String path = createPath(storedFileName);

        file.transferTo(new File(path));

        ImageFile imageFile = new ImageFile(originalName, storedFileName, keyword);

        imageFileRepository.save(imageFile);
        return imageFile;
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

    //확장자를 추출하는 메서드
    private String extractExt(String originalName) {
        int idx = originalName.lastIndexOf(".");
        return originalName.substring(idx);
    }

    //storedFileName 설정하는 메서드
    private String createStoredFileName(String originalName) {
        String uuid = UUID.randomUUID().toString();
        String ext = extractExt(originalName);

        return uuid + ext;
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
