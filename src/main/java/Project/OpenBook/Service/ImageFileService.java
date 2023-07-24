package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.ImageFile;
import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Repository.imagefile.ImageFileRepository;
import Project.OpenBook.Utils.CustomException;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageFileService {

    private final ImageFileRepository imageFileRepository;
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    public ImageFile storeFile(String encodedFile, Keyword keyword) throws IOException {

        String[] parts = encodedFile.split(",");
        String header = parts[0];
        String encodedImage = parts[1]; // "data:image/png;base64," 부분을 제외한 이미지 인코딩 값

        String ext = extractExtension(header);
        // 이미지 디코드
        byte[] decodedBytes = Base64.getDecoder().decode(encodedImage);
        InputStream inputStream = new ByteArrayInputStream(decodedBytes);

        String storedFileName = createStoredFileName(ext);
        String imageUrl = createPath(storedFileName);

        // 이미지 파일 저장
        amazonS3Client.putObject(bucket, storedFileName, inputStream, null);

        ImageFile imageFile = new ImageFile(imageUrl, keyword);

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
        imageFileRepository.deleteAllInBatch(imageFileList);
        return true;
    }

    //storedFileName 설정하는 메서드
    private String createStoredFileName(String ext) {
        String uuid = UUID.randomUUID().toString();
        return uuid +"."+ ext;
    }

    //이미지를 저장할 파일 경로 설정하는 메서드
    private String createPath(String storedFileName) {
        return "https://" + bucket + ".s3.ap-northeast-2.amazonaws.com/" + storedFileName;
    }

    public Boolean checkBase64(String encodedFile) {
        if (encodedFile == null || encodedFile.equals("")) {
            return false;
        }

        try {
            String[] parts = encodedFile.split(",");
            if (parts.length != 2) {
                return false;
            }

            String encodedImage = parts[1]; // "data:image/png;base64," 부분을 제외한 이미지 인코딩 값
            byte[] decode = Base64.getDecoder().decode(encodedImage);
            if (decode != null) {
                return true;
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public String findImageUrl(Long imageId) {
        ImageFile imageFile = imageFileRepository.findById(imageId).orElseThrow(() -> {
            throw new CustomException(ErrorCode.IMAGE_NOT_FOUND);
        });

        return imageFile.getImageUrl();
    }
}
