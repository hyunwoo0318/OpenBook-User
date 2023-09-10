package Project.OpenBook.Service;

import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Topic.Domain.Topic;
import Project.OpenBook.Dto.keyword.KeywordCreateDto;
import Project.OpenBook.Dto.keyword.KeywordUserDto;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Topic.Repo.TopicRepository;
import Project.OpenBook.Utils.CustomException;
import com.amazonaws.services.s3.AmazonS3Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;

    private final AmazonS3Client amazonS3Client;
    private final TopicRepository topicRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public List<String> queryKeywords() {
        return keywordRepository.findAll().stream().map(k -> k.getName()).collect(Collectors.toList());
    }

    public Keyword createKeyword(KeywordCreateDto keywordCreateDto) throws IOException {

        String name = keywordCreateDto.getName();
        String comment = keywordCreateDto.getComment();
        String topicTitle = keywordCreateDto.getTopic();
        String encodedFile = keywordCreateDto.getFile();
        String imageUrl = null;

        //입력 받은 토픽 제목이 실제 존재하는 토픽 제목인지 확인
        Topic topic = topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });

        //같은 토픽 내의 같은 이름의 키워드가 있는지 확인
        checkDupKeyword(name, topicTitle);

        //이미지 저장
        if (checkBase64(encodedFile)) {
            imageUrl = storeFile(encodedFile);
        }

        //키워드 저장
        Keyword keyword = new Keyword(name,comment, topic,imageUrl);
        keywordRepository.save(keyword);

        return keyword;
    }


    public Keyword updateKeyword(Long keywordId, KeywordUserDto keywordUserDto) throws IOException {

        String name = keywordUserDto.getName();
        String comment = keywordUserDto.getComment();
        String encodedFile = keywordUserDto.getFile();

        Keyword keyword = checkKeyword(keywordId);
        String title = keyword.getTopic().getTitle();
        String newImageUrl = keyword.getImageUrl();

        //키워드 이름을 변경시 중복되는 키워드 이름이 있는지 확인해야함
        //키워드 이름이 같을 경우 확인할 필요없음.
        if (!keyword.getName().equals(name)) {
            checkDupKeyword(name, title);
        }

        //이미지 수정
        if (checkBase64(encodedFile)) {
            newImageUrl = storeFile(encodedFile);
        }

        //키워드 수정
        Keyword afterKeyword = keyword.updateKeyword(name, comment,newImageUrl);
        return afterKeyword;
    }


    private void checkDupKeyword(String name, String topicTitle) {
        if (keywordRepository.queryByNameInTopic(name, topicTitle).isPresent()) {
            throw new CustomException(DUP_KEYWORD_NAME);
        }
    }

    private Keyword checkKeyword(Long id) {
        return keywordRepository.findById(id).orElseThrow(() -> {
            throw new CustomException(KEYWORD_NOT_FOUND);
        });
    }

    public void deleteKeyword(Long keywordId) {
        checkKeyword(keywordId);

        keywordRepository.deleteById(keywordId);
    }

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
}
