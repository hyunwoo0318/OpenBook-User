package Project.OpenBook.Image;

import Project.OpenBook.Handler.Exception.CustomException;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.UUID;

import static Project.OpenBook.Constants.ErrorCode.IMAGE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ImageService {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String storeFile(String encodedFile) throws IOException {

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

        return imageUrl;
    }

    private String extractExtension(String header) {
        String[] parts = header.split("/");
        String mediaType = parts[1];
        String[] subParts = mediaType.split(";");
        return subParts[0];
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

    public void checkBase64(String encodedFile) {
        if (encodedFile == null || encodedFile.equals("")) {
            throw new CustomException(IMAGE_NOT_FOUND);
        }

        try {
            String[] parts = encodedFile.split(",");
            if (parts.length != 2) {
                throw new CustomException(IMAGE_NOT_FOUND);
            }

            String encodedImage = parts[1]; // "data:image/png;base64," 부분을 제외한 이미지 인코딩 값
            byte[] decode = Base64.getDecoder().decode(encodedImage);
            if (decode != null) {
                throw new CustomException(IMAGE_NOT_FOUND);
            }
            throw new CustomException(IMAGE_NOT_FOUND);
        } catch (Exception e) {
            throw new CustomException(IMAGE_NOT_FOUND);
        }
    }
}
