package Project.OpenBook.Service;

import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.keyword.KeywordCreateDto;
import Project.OpenBook.Dto.keyword.KeywordUpdateDto;
import Project.OpenBook.Repository.imagefile.ImageFileRepository;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final ImageFileRepository imageFileRepository;
    private final TopicRepository topicRepository;
    private final ImageFileService imageFileService;

    public List<String> queryKeywords() {
        return keywordRepository.findAll().stream().map(k -> k.getName()).collect(Collectors.toList());
    }

    public Keyword createKeyword(KeywordCreateDto keywordCreateDto) throws IOException {

        String name = keywordCreateDto.getName();
        String comment = keywordCreateDto.getComment();
        String topicTitle = keywordCreateDto.getTopic();
        String encodedFile = keywordCreateDto.getFile();
/*
        List<String> fileList = keywordCreateDto.getFileList();
*/

        //입력 받은 토픽 제목이 실제 존재하는 토픽 제목인지 확인
        Topic topic = topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });

        //같은 토픽 내의 같은 이름의 키워드가 있는지 확인
        checkDupKeyword(name, topicTitle);

        //키워드 저장
        Keyword keyword = new Keyword(name,comment, topic);
        keywordRepository.save(keyword);

        //이미지 저장
//        for (String encodedFile : fileList) {
//            imageFileService.storeFile(encodedFile, keyword);
//        }
        imageFileService.storeFile(encodedFile, keyword);

        return keyword;
    }


    public Keyword updateKeyword(Long keywordId, KeywordUpdateDto keywordUpdateDto) throws IOException {

        String name = keywordUpdateDto.getName();
        String comment = keywordUpdateDto.getComment();
//        List<String> fileList = keywordUpdateDto.getFileList();
        String encodedFile = keywordUpdateDto.getFile();

        Keyword keyword = checkKeyword(keywordId);
        String title = keyword.getTopic().getTitle();

        //키워드 이름을 변경시 중복되는 키워드 이름이 있는지 확인해야함
        //키워드 이름이 같을 경우 확인할 필요없음.
        if (!keyword.getName().equals(name)) {
            checkDupKeyword(name, title);
        }

        //키워드 수정
        Keyword afterKeyword = keyword.updateKeyword(name, comment);

        //이미지 수정
        imageFileService.deleteImages(keywordId);
//        for (String encodedFile : fileList) {
//            imageFileService.storeFile(encodedFile, afterKeyword);
//        }
        imageFileService.storeFile(encodedFile, afterKeyword);
        return afterKeyword;
    }


    private void checkDupKeyword(String name, String topicTitle) {
        if (keywordRepository.queryByNameInTopic(name, topicTitle) != null) {
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

        imageFileService.deleteImages(keywordId);
        keywordRepository.deleteById(keywordId);
    }
}
