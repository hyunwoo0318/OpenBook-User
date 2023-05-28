package Project.OpenBook.Service;

import Project.OpenBook.Dto.keyword.KeywordListDto;
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

    public TopicDto queryTopic(String topicTitle) {
        Topic topic = checkTopic(topicTitle);

        TopicDto topicDto = new TopicDto(topic.getChapter().getNumber(), topic.getTitle(), topic.getCategory().getName(), topic.getStartDate(), topic.getEndDate()
                , topic.getDetail());
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

        addDupDates(topic);
        return topic;
    }

    public Topic updateTopic(String topicTitle, TopicDto topicDto) {
        Topic topic = checkTopic(topicTitle);

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
            addDupDates(topic);
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

    private void addDupDates(Topic topic) {
        List<Topic> topicList = dupDateRepository.queryTopicsByDupDate(topic.getStartDate(), topic.getEndDate());
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

    public void addKeywords(String topicTitle, KeywordListDto keywordListDto) {
        Topic topic = checkTopic(topicTitle);

        List<Keyword> keywordList = keywordRepository.queryKeywordsList(keywordListDto.getKeywordList());
        if(keywordList.size() != keywordListDto.getKeywordList().size()){
            throw new CustomException(KEYWORD_NOT_FOUND);
        }
        /**
         * TODO : 기존에 있는 경우 추가하지 않는 로직 추가
         */

        List<TopicKeyword> topicKeywordList = keywordList.stream().map(k -> new TopicKeyword(topic, k)).collect(Collectors.toList());
        topicKeywordRepository.saveAll(topicKeywordList);
    }

    public void deleteKeyword(String topicTitle, String keywordName) {
        checkTopic(topicTitle);
        checkKeyword(keywordName);

        topicKeywordRepository.deleteTopicKeyword(topicTitle, keywordName);
    }
}
