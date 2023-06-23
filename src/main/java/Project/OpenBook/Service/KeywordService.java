package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.topickeyword.TopicKeywordRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordRepository keywordRepository;
    private final TopicKeywordRepository topicKeywordRepository;

    public List<String> queryKeywords() {
        return keywordRepository.findAll().stream().map(k -> k.getName()).collect(Collectors.toList());
    }

    public Keyword createKeyword(String name) {
        checkDupKeyword(name);

        Keyword keyword = new Keyword(name);
        keywordRepository.save(keyword);
        return keyword;
    }


    public Keyword updateKeyword(String prevName, String afterName) {
        Keyword keyword = checkKeyword(prevName);
        checkDupKeyword(afterName);

        keyword.changeName(afterName);
        return keyword;
    }


    public void deleteKeyword(String keywordName) {
        Keyword keyword = checkKeyword(keywordName);
        List<String> topicList = keywordRepository.queryKeywordTopic(keywordName);
        if(!topicList.isEmpty()){
            throw new CustomException(KEYWORD_HAS_TOPIC);
        }
        keywordRepository.delete(keyword);
    }

    public List<String> queryKeywordTopic(String keywordName) {
        checkKeyword(keywordName);
        return keywordRepository.queryKeywordTopic(keywordName);
    }


    private void checkDupKeyword(String name) {
        keywordRepository.findByName(name).ifPresent(k -> {
            throw new CustomException(DUP_KEYWORD_NAME);
        });
    }

    private Keyword checkKeyword(String name) {
        return keywordRepository.findByName(name).orElseThrow(() -> {
            throw new CustomException(KEYWORD_NOT_FOUND);
        });
    }
}
