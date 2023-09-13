package Project.OpenBook.Domain.Topic.Service;

import Project.OpenBook.Domain.Category.Domain.Category;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Constants.StateConst;

import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.PrimaryDate.Domain.PrimaryDate;
import Project.OpenBook.Domain.PrimaryDate.Dto.PrimaryDateDto;
import Project.OpenBook.Domain.PrimaryDate.Repository.PrimaryDateRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.Service.dto.TopicDetailDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicNumberDto;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.StudyProgress.TopicProgress.Repository.TopicProgressRepository;
import Project.OpenBook.Domain.StudyProgress.TopicProgress.Domain.TopicProgress;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Domain.Category.Repository.CategoryRepository;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import java.util.List;
import java.util.stream.Collectors;


import static Project.OpenBook.Constants.ErrorCode.*;
@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;
    private final CustomerRepository customerRepository;
    private final PrimaryDateRepository primaryDateRepository;
    private final TopicProgressRepository topicProgressRepository;
    private final TopicValidator topicValidator;


    public Topic queryTopicWithCategoryChapter(String topicTitle) {
        return topicRepository.queryTopicWithCategoryChapter(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    public Topic queryTopicWithCategory(String topicTitle) {
        return topicRepository.queryTopicWithCategory(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    @Transactional
    public void createTopic(TopicDetailDto topicDetailDto) {

        Category category = checkCategory(topicDetailDto.getCategory());
        Chapter chapter = checkChapter(topicDetailDto.getChapter());
        topicValidator.checkDupTopicTitle(topicDetailDto.getTitle());

        //토픽 저장
        Topic topic = Topic.builder()
                .chapter(chapter)
                .category(category)
                .title(topicDetailDto.getTitle())
                .startDate(topicDetailDto.getStartDate())
                .endDate(topicDetailDto.getEndDate())
                .detail(topicDetailDto.getDetail())
                .startDateCheck(topicDetailDto.getStartDateCheck())
                .endDateCheck(topicDetailDto.getEndDateCheck())
                .questionNum(0)
                .choiceNum(0)
                .build();
        topicRepository.save(topic);

        //연표에 표시할 날짜 저장
        List<PrimaryDateDto> dateList = new ArrayList<>();
        if (topicDetailDto.getExtraDateList() != null) {
            dateList = topicDetailDto.getExtraDateList();
        }
        List<PrimaryDate> primaryDateList = dateList.stream().map(d -> new PrimaryDate(d.getExtraDate(), d.getExtraDateCheck(), d.getExtraDateComment(), topic))
                .collect(Collectors.toList());
        primaryDateRepository.saveAll(primaryDateList);

        //주제학습 레코드 생성
        updateTopicProgress(topic);
    }

    private void updateTopicProgress(Topic topic) {
        List<TopicProgress> topicProgressList = customerRepository.findAll().stream()
                .map(c -> new TopicProgress(c, topic,0, StateConst.LOCKED.getName()))
                .collect(Collectors.toList());
        topicProgressRepository.saveAll(topicProgressList);
    }


    @Transactional
    public void updateTopic(String topicTitle, TopicDetailDto topicDetailDto) {
        Topic topic = topicValidator.checkTopic(topicTitle);
        String inputTitle = topicDetailDto.getTitle();

        if(!topicTitle.equals(inputTitle)){
            //새로 입력받은 제목이 중복되는지 확인
            topicValidator.checkDupTopicTitle(inputTitle);
        }
        String categoryName = topicDetailDto.getCategory();
        Category category = checkCategory(categoryName);

        int chapterNum = topicDetailDto.getChapter();
        Chapter chapter = checkChapter(chapterNum);


        //토픽 수정
        topic.updateTopic(topicDetailDto.getTitle(), topicDetailDto.getStartDate(), topicDetailDto.getEndDate(), topicDetailDto.getStartDateCheck(),
                topicDetailDto.getEndDateCheck(), topicDetailDto.getDetail(), chapter, category);

        //연표에 나올 날짜 수정
        List<PrimaryDate> prevDateList = topic.getPrimaryDateList();
        primaryDateRepository.deleteAllInBatch(prevDateList);
        List<PrimaryDate> primaryDateList = topicDetailDto.getExtraDateList().stream()
                .map(d -> new PrimaryDate(d.getExtraDate(), d.getExtraDateCheck(), d.getExtraDateComment(), topic))
                .collect(Collectors.toList());
        primaryDateRepository.saveAll(primaryDateList);
    }

    @Transactional
    public void deleteTopic(String topicTitle) {
        Topic findTopic = queryTopicWithCategoryChapter(topicTitle);

        List<Choice> choiceList = findTopic.getChoiceList();
        if (!choiceList.isEmpty()) {
            throw new CustomException(TOPIC_HAS_CHOICE);
        }

        List<Description> descriptionList = findTopic.getDescriptionList();
        if (!descriptionList.isEmpty()) {
            throw new CustomException(TOPIC_HAS_DESCRIPTION);
        }
        List<Keyword> keywordList = findTopic.getKeywordList();
        if (!keywordList.isEmpty()) {
            throw new CustomException(TOPIC_HAS_KEYWORD);
        }

        List<Sentence> sentenceList = findTopic.getSentenceList();
        if (!sentenceList.isEmpty()) {
            throw new CustomException(TOPIC_HAS_SENTENCE);
        }

        List<PrimaryDate> primaryDateList = findTopic.getPrimaryDateList();
        primaryDateRepository.deleteAllInBatch(primaryDateList);

        topicRepository.delete(findTopic);
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
    @Transactional
    public void updateTopicNumber(List<TopicNumberDto> topicNumberDtoList) {
        for (TopicNumberDto topicNumberDto : topicNumberDtoList) {
            Topic topic = topicValidator.checkTopic(topicNumberDto.getTitle());
            topic.updateTopicNumber(topicNumberDto.getNumber());
        }
    }


}
