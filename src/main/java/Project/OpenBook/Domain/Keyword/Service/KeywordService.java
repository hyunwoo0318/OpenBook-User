package Project.OpenBook.Domain.Keyword.Service;

import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Dto.KeywordCreateDto;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Keyword.Dto.KeywordUserDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Image.ImageService;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final TopicRepository topicRepository;
    private final ImageService imageService;


    @Transactional
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
        if(encodedFile != null && !encodedFile.isBlank()){
            imageService.checkBase64(encodedFile);
            imageUrl = imageService.storeFile(encodedFile);
        }


        //키워드 저장
        Keyword keyword = new Keyword(name,comment, topic,imageUrl);
        keywordRepository.save(keyword);

        return keyword;
    }


    @Transactional
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
        if(encodedFile != null && !encodedFile.isBlank() &&!encodedFile.startsWith("https")){
            imageService.checkBase64(encodedFile);
            newImageUrl = imageService.storeFile(encodedFile);
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

    @Transactional
    public void deleteKeyword(Long keywordId) {
        checkKeyword(keywordId);

        keywordRepository.deleteById(keywordId);
    }


}
