package Project.OpenBook.Service;

import Project.OpenBook.CustomException;
import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.topic.TopicDto;
import Project.OpenBook.Repository.CategoryRepository;
import Project.OpenBook.Repository.ChapterRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.util.Arrays;

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
        return topic;
    }

    public Topic updateTopic(String topicTitle, TopicDto topicDto) {
        Topic topic = checkTopic(topicTitle);

        String categoryName = topicDto.getCategory();
        Category category = checkCategory(categoryName);

        int chapterNum = topicDto.getChapter();
        Chapter chapter = checkChapter(chapterNum);

        topic.updateTopic(topicDto.getTitle(), topicDto.getStartDate(),topicDto.getEndDate(), topicDto.getDetail(),
                chapter, category);

        return topic;
    }

    public boolean deleteTopic(String topicTitle) {
        Topic topic = checkTopic(topicTitle);

        topicRepository.delete(topic);
        return true;

    }

    public String parseKeywordList(List<String> keywordList) {
      return keywordList.stream().collect(Collectors.joining(","));
    }

    public List<String> mergeKeywordList(String keywords) {
        String[] split = keywords.split(",");
        return Arrays.asList(split);
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
}
