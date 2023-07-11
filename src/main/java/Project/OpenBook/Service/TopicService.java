package Project.OpenBook.Service;

import Project.OpenBook.Dto.keyword.KeywordDto;
import Project.OpenBook.Dto.keyword.KeywordListDto;
import Project.OpenBook.Repository.Sentence.SentenceRepository;
import Project.OpenBook.Repository.topickeyword.TopicKeywordRepository;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.topic.TopicDto;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.dupdate.DupDateRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.ArrayList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;

    private final DupDateRepository dupDateRepository;

    private final ChoiceRepository choiceRepository;

    private final DescriptionRepository descriptionRepository;

    private final KeywordRepository keywordRepository;
    private final TopicKeywordRepository topicKeywordRepository;

    private final SentenceRepository  sentenceRepository;

    public TopicDto queryTopic(String topicTitle) {
        checkTopic(topicTitle);
        TopicDto topicDto = topicRepository.queryTopicDto(topicTitle);
        return topicDto;
    }

    public Topic createTopic(TopicDto topicDto) {

        Category category = checkCategory(topicDto.getCategory());
        Chapter chapter = checkChapter(topicDto.getChapter());
        checkDupTopicTitle(topicDto.getTitle());

        Topic topic = Topic.builder()
                .chapter(chapter)
                .category(category)
                .title(topicDto.getTitle())
                .startDate(topicDto.getStartDate())
                .endDate(topicDto.getEndDate())
                .detail(topicDto.getDetail())
                .questionNum(0)
                .choiceNum(0)
                .build();
        topicRepository.save(topic);

        addDescriptionTopics(topic);
        addAnswerTopics(topic);
        return topic;
    }

    public Topic updateTopic(String topicTitle, TopicDto topicDto) {
        Topic topic = checkTopic(topicTitle);
        String inputTitle = topicDto.getTitle();

        if(!topicTitle.equals(inputTitle)){
            //새로 입력받은 제목이 중복되는지 확인
            checkDupTopicTitle(inputTitle);
        }
        String categoryName = topicDto.getCategory();
        Category category = checkCategory(categoryName);

        int chapterNum = topicDto.getChapter();
        Chapter chapter = checkChapter(chapterNum);

        boolean flag = false;

        if(topic.getStartDate()!=topicDto.getStartDate() || topic.getEndDate() != topicDto.getEndDate()){
            flag = true;
        }

        topic.updateTopic(topicDto.getTitle(), topicDto.getStartDate(),topicDto.getEndDate(), topicDto.getDetail(),
                chapter, category);

        if (flag) {
            List<DupDate> dupDateList = dupDateRepository.queryAllByTopic(topic.getTitle());
            dupDateRepository.deleteAllInBatch(dupDateList);
            addDescriptionTopics(topic);
            addAnswerTopics(topic);
        }

        return topic;
    }

    public boolean deleteTopic(String topicTitle) {
        Topic topic = checkTopic(topicTitle);

        List<Choice> choiceList = choiceRepository.queryChoiceByTopicTitle(topicTitle);
        if (!choiceList.isEmpty()) {
            throw new CustomException(TOPIC_HAS_CHOICE);
        }

        List<Description> descriptionList = descriptionRepository.findDescriptionsByTopic(topicTitle);
        if (!descriptionList.isEmpty()) {
            throw new CustomException(TOPIC_HAS_DESCRIPTION);
        }

        List<TopicKeyword> topicKeywordList = topicKeywordRepository.queryTopicKeyword(topicTitle);
        if (!topicKeywordList.isEmpty()) {
            throw new CustomException(TOPIC_HAS_KEYWORD);
        }

        topicRepository.delete(topic);
        return true;

    }

//    public String parseKeywordList(List<String> keywordList) {
//      return keywordList.stream().collect(Collectors.joining(","));
//    }
//
//    public List<String> mergeKeywordList(String keywords) {
//        String[] split = keywords.split(",");
//        return Arrays.asList(split);
//    }

    private void addAnswerTopics(Topic topic) {
        List<Topic> topicList = dupDateRepository.queryAnswerTopics(topic.getStartDate(), topic.getEndDate());
        List<DupDate> dupDateList = new ArrayList<>();

        for (Topic descriptionTopic : topicList) {
            dupDateList.add(new DupDate(topic, descriptionTopic));
        }
        dupDateRepository.saveAll(dupDateList);
    }

    private void addDescriptionTopics(Topic topic) {
        List<Topic> topicList = dupDateRepository.queryDescriptionTopics(topic.getStartDate(), topic.getEndDate());
        List<DupDate> dupDateList = new ArrayList<>();

        for (Topic descriptionTopic : topicList) {
            dupDateList.add(new DupDate(topic, descriptionTopic));
        }
        dupDateRepository.saveAll(dupDateList);
    }

    private Chapter checkChapter(int num) {
        return chapterRepository.findOneByNumber(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });
    }



    private Category checkCategory(String categoryName) {
        return categoryRepository.findCategoryByName(categoryName).orElseThrow(() -> {
            throw new CustomException(CATEGORY_NOT_FOUND);
        });
    }

    private Keyword checkKeyword(String keywordName) {
        return keywordRepository.findByName(keywordName).orElseThrow(() -> {
            throw new CustomException(KEYWORD_NOT_FOUND);
        });
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    private void checkDupTopicTitle(String topicTitle) {
        topicRepository.findTopicByTitle(topicTitle).ifPresent(t -> {
            throw new CustomException(DUP_TOPIC_TITLE);
        });
    }

    public List<String> queryTopicKeywords(String topicTitle) {
        checkTopic(topicTitle);
        return topicRepository.queryTopicKeywords(topicTitle);
    }

    public void addKeywords(String topicTitle, KeywordDto keywordDto) {
        Topic topic = checkTopic(topicTitle);
        String name = keywordDto.getName();
        Keyword keyword;

        //입력받은 키워드가 새로운 키워드인 경우와 기존의 키워드인 경우를 구분
        Optional<Keyword> keywordOptional = keywordRepository.findByName(name);
        //기존에 존재하는 키워드의 경우
        if (keywordOptional.isPresent()) {
            keyword = keywordOptional.get();
            // 이미 해당 토픽에 해당 키워드가 있는 경우 해당 키워드를 추가하지 않음.
            TopicKeyword topicKeyword = topicKeywordRepository.queryTopicKeyword(topicTitle, name);
            if (topicKeyword == null) {
                topicKeywordRepository.save(new TopicKeyword(topic, keyword));
            }
        }else{
            //새로운 키워드의 경우
            //새로 키워드를 만들고 저장후 해당 토픽에 해당 키워드를 저장
            keyword = new Keyword(name);
            keywordRepository.save(keyword);
            topicKeywordRepository.save(new TopicKeyword(topic, keyword));
        }
    }

    @Transactional
    public void deleteKeyword(String topicTitle, String keywordName) {
        Topic topic = checkTopic(topicTitle);

        Keyword keyword = checkKeyword(keywordName);

        Optional<TopicKeyword> topicKeywordOptional = topicKeywordRepository.findByTopicAndKeyword(topic, keyword);
        if (topicKeywordOptional.isPresent()) {
            TopicKeyword topicKeyword = topicKeywordOptional.get();
            topicKeywordRepository.delete(topicKeyword);
            List<Topic> topicList = topicKeywordRepository.queryTopicsByKeyword(keywordName);
            if (topicList.isEmpty()) {
                keywordRepository.delete(keyword);
            }
        }
    }

    public List<Sentence> queryTopicSentences(String topicTitle) {
        checkTopic(topicTitle);
        return sentenceRepository.queryByTopicTitle(topicTitle);
    }
}
