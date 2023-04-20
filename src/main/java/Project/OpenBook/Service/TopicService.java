package Project.OpenBook.Service;

import Project.OpenBook.Domain.Category;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.ErrorDto;
import Project.OpenBook.Dto.TopicDto;
import Project.OpenBook.Repository.CategoryRepository;
import Project.OpenBook.Repository.ChapterRepository;
import Project.OpenBook.Repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;

    private final ChapterRepository chapterRepository;

    public TopicDto queryTopic(String topicTitle) {
        Optional<Topic> topicOptional = topicRepository.findTopicByTitle(topicTitle);
        if (topicOptional.isEmpty()) {
            return null;
        }
        Topic topic = topicOptional.get();

        TopicDto topicDto = new TopicDto(topic.getChapter().getNumber(), topic.getTitle(), topic.getCategory().getName(), topic.getStartDate(), topic.getEndDate()
                , topic.getDetail());
        return topicDto;
    }

    public Topic createTopic(TopicDto topicDto, List<ErrorDto> errorDtoList) {

        String categoryName = topicDto.getCategory();
        Optional<Category> categoryOptional = categoryRepository.findCategoryByName(categoryName);
        if (categoryOptional.isEmpty()) {
            errorDtoList.add(new ErrorDto("category", "존재하지않는 카테고리입니다."));
        }

        int chapterNum = topicDto.getChapter();
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(chapterNum);
        if (chapterOptional.isEmpty()) {
            errorDtoList.add(new ErrorDto("chapter", "존재하지않는 단원입니다."));
        }

        Optional<Topic> topicOptional = topicRepository.findTopicByTitle(topicDto.getTitle());
        if (topicOptional.isPresent()) {
            errorDtoList.add(new ErrorDto("title", "이미 존재하는 제목입니다."));
        }

        //오류가 있으면 따로 return
        if(!errorDtoList.isEmpty())
            return null;

        //입력이 필수가 아닌 startDate, endDate, keywords처리
        //keywordList -> keywords

        /*String keywords = "";
        if(topicDto.getKeywordList() != null){
           keywords  = parseKeywordList(topicDto.getKeywordList());
        }*/


        Topic topic = Topic.builder()
                .chapter(chapterOptional.get())
                .category(categoryOptional.get())
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

    public Topic updateTopic(String topicTitle, TopicDto topicDto, List<ErrorDto> errorDtoList) {
        Optional<Topic> topicOptional = topicRepository.findTopicByTitle(topicTitle);
        if (topicOptional.isEmpty()) {
            return null;
        }

        String categoryName = topicDto.getCategory();
        Optional<Category> categoryOptional = categoryRepository.findCategoryByName(categoryName);
        if (categoryOptional.isEmpty()) {
            errorDtoList.add(new ErrorDto("category", "존재하지않는 카테고리입니다."));
        }

        int chapterNum = topicDto.getChapter();
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(chapterNum);
        if (chapterOptional.isEmpty()) {
            errorDtoList.add(new ErrorDto("chapter", "존재하지않는 단원입니다."));
        }

        //오류가 있으면 따로 return
        if(!errorDtoList.isEmpty())
            return null;


       /* String keywords = "";
        if(topicDto.getKeywordList() != null){
            keywords  = parseKeywordList(topicDto.getKeywordList());
        }*/

        Topic topic = topicOptional.get();
        topic.updateTopic(topicDto.getTitle(), topicDto.getStartDate(),topicDto.getEndDate(), topicDto.getDetail(),
                chapterOptional.get(), categoryOptional.get());

        return topic;
    }

    public boolean deleteTopic(String topicTitle) {
        Optional<Topic> topicOptional = topicRepository.findTopicByTitle(topicTitle);
        if (topicOptional.isEmpty()) {
            return false;
        }

        topicRepository.delete(topicOptional.get());
        return true;

    }

    public String parseKeywordList(List<String> keywordList) {
      return keywordList.stream().collect(Collectors.joining(","));
    }

    public List<String> mergeKeywordList(String keywords) {
        String[] split = keywords.split(",");
        return Arrays.asList(split);
    }
}
