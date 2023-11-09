package Project.OpenBook.Domain.Keyword.Service;

import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeywordRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Domain.KeywordPrimaryDate;
import Project.OpenBook.Domain.Keyword.KeywordPrimaryDate.Repository.KeywordPrimaryDateRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordCreateDto;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordNumberDto;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordUserDto;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordWithTopicDto;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearch;
import Project.OpenBook.Domain.Search.KeywordSearch.KeywordSearchRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final KeywordSearchRepository keywordSearchRepository;
    private final DescriptionKeywordRepository descriptionKeywordRepository;
    private final KeywordPrimaryDateRepository keywordPrimaryDateRepository;
    private final TopicRepository topicRepository;
    private final ImageService imageService;


    @Transactional(readOnly = true)
    public List<KeywordWithTopicDto> queryTotalKeywords() {
        return keywordRepository.queryKeywordsWithTopic().stream()
                .map(k -> new KeywordWithTopicDto(k.getName(), k.getTopic().getTitle(), k.getId()))
                .collect(Collectors.toList());
    }


    @Transactional
    public Keyword createKeyword(KeywordCreateDto keywordCreateDto) throws IOException {

        String name = keywordCreateDto.getName();
        String comment = keywordCreateDto.getComment();
        String topicTitle = keywordCreateDto.getTopic();
        String encodedFile = keywordCreateDto.getFile();
        Integer number = keywordCreateDto.getNumber();
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
        Keyword keyword = new Keyword(number,name,comment, keywordCreateDto.getDateComment(),topic,imageUrl);
        keywordRepository.save(keyword);
        keywordSearchRepository.save(new KeywordSearch(keyword));

        //추가년도 저장
        List<KeywordPrimaryDate> keywordPrimaryDateList = keywordCreateDto.getExtraDateList().stream()
                .map(d -> new KeywordPrimaryDate(d.getExtraDate(), d.getExtraDateComment(), keyword))
                .collect(Collectors.toList());

        keywordPrimaryDateRepository.saveAll(keywordPrimaryDateList);

        return keyword;
    }


    @Transactional
    public Keyword updateKeyword(Long keywordId, KeywordUserDto keywordUserDto) throws IOException {

        String name = keywordUserDto.getName();
        String comment = keywordUserDto.getComment();
        String encodedFile = keywordUserDto.getFile();
        Integer number = keywordUserDto.getNumber();

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
        } else if (encodedFile != null && encodedFile.isBlank()) {
            //기존 이미지 삭제
            newImageUrl = null;
        }

        //키워드 수정
        Keyword afterKeyword = keyword.updateKeyword(number, name, comment, keywordUserDto.getDateComment(),newImageUrl);
        keywordSearchRepository.save(new KeywordSearch(afterKeyword));

        //추가년도 수정
        List<KeywordPrimaryDate> prevKeywordPrimaryDateList = afterKeyword.getKeywordPrimaryDateList();
        keywordPrimaryDateRepository.deleteAllInBatch(prevKeywordPrimaryDateList);
        List<KeywordPrimaryDate> newKeywordPrimaryDateList = keywordUserDto.getExtraDateList().stream()
                .map(p -> new KeywordPrimaryDate(p.getExtraDate(), p.getExtraDateComment(), afterKeyword))
                .collect(Collectors.toList());
        keywordPrimaryDateRepository.saveAll(newKeywordPrimaryDateList);

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
        Keyword keyword = checkKeyword(keywordId);
        descriptionKeywordRepository.deleteByKeyword(keyword);
        keywordRepository.deleteById(keywordId);
    }


    @Transactional
    public void updateKeywordNumbers(List<KeywordNumberDto> keywordNumberDtoList) {
        Map<Long, Integer> m = new HashMap<>();
        List<Long> keywordIdList = new ArrayList<>();
        for (KeywordNumberDto keywordNumberDto : keywordNumberDtoList) {
            m.put(keywordNumberDto.getId(), keywordNumberDto.getNumber());
            keywordIdList.add(keywordNumberDto.getId());
        }
        List<Keyword> keywordList = keywordRepository.findAllById(keywordIdList);
        if (keywordList.size() != keywordNumberDtoList.size()) {
            throw new CustomException(KEYWORD_NOT_FOUND);
        }

        for (Keyword keyword : keywordList) {
            Long keywordId = keyword.getId();
            Integer number = m.get(keywordId);
            keyword.updateNumber(number);
        }

    }


}
