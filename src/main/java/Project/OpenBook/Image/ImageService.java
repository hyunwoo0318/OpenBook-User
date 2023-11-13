package Project.OpenBook.Image;

import Project.OpenBook.Handler.Exception.CustomException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import lombok.RequiredArgsConstructor;
import marvin.image.MarvinImage;
import org.marvinproject.image.transform.scale.Scale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;
import java.util.UUID;

import static Project.OpenBook.Constants.ErrorCode.IMAGE_SAVE_FAIL;
import static Project.OpenBook.Constants.ErrorCode.NOT_VALIDATE_IMAGE;

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

        String storedFileName = createStoredFileName(ext);
        String imageUrl = createPath(storedFileName);

        InputStream inputStream = resizePicture(ext, decodedBytes);

        saveImage(storedFileName, inputStream);

        return imageUrl;
    }

    public void saveImage(String storedFileName, InputStream inputStream) throws IOException {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        byte[] bytes = IOUtils.toByteArray(inputStream);
        objectMetadata.setContentLength(bytes.length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        // 이미지 파일 저장
        try {
            amazonS3Client.putObject(bucket, storedFileName, byteArrayInputStream,objectMetadata);
            System.out.println("IMAGE SAVE SUCCESS!");
        } catch (Exception e) {
            throw new CustomException(IMAGE_SAVE_FAIL);
        }
    }

    public InputStream resizePicture(String ext, byte[] decodedBytes) throws IOException {
        int targetWidth = 1000;
        InputStream inputStream = new ByteArrayInputStream(decodedBytes);

        BufferedImage image = ImageIO.read(inputStream);
        int originWidth = image.getWidth();
        int originHeight = image.getHeight();

        MarvinImage imageMarvin = new MarvinImage(image);

        Scale scale = new Scale();
        scale.load();
        scale.setAttribute("newWidth", targetWidth);
        scale.setAttribute("newHeight", targetWidth * originHeight / originWidth);
        scale.process(imageMarvin.clone(), imageMarvin, null, null, false);

        BufferedImage bufferedImage = imageMarvin.getBufferedImageNoAlpha();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, ext, os);
        return new ByteArrayInputStream(os.toByteArray());
    }

    public byte[] downloadFile(String fileName) throws FileNotFoundException {

        //파일 유무 확인
//        validateFileExists(fileName);
        S3Object s3Object = amazonS3Client.getObject(bucket, fileName);
        S3ObjectInputStream s3ObjectContent = s3Object.getObjectContent();

        try {
            return IOUtils.toByteArray(s3ObjectContent);
        }catch (IOException e ){
            throw new CustomException(IMAGE_SAVE_FAIL);
        }
    }


//    private void validateFileExists(String fileName) throws FileNotFoundException {
//        if(!amazonS3Client.doesObjectExist(bucket, fileName))
//            throw new FileNotFoundException();
//    }

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
            throw new CustomException(NOT_VALIDATE_IMAGE);
        }

        try {
            String[] parts = encodedFile.split(",");
            if (parts.length != 2) {
                throw new CustomException(NOT_VALIDATE_IMAGE);
            }

            String encodedImage = parts[1]; // "data:image/png;base64," 부분을 제외한 이미지 인코딩 값
            byte[] decode = Base64.getDecoder().decode(encodedImage);
            if (decode == null) {
                throw new CustomException(NOT_VALIDATE_IMAGE);
            }
        } catch (Exception e) {
            throw new CustomException(NOT_VALIDATE_IMAGE);
        }
    }


}
