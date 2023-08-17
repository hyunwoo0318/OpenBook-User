package Project.OpenBook.Service;

import Project.OpenBook.Dto.PrimaryDate.PrimaryDateDto;
import Project.OpenBook.Dto.PrimaryDate.PrimaryDateUserDto;
import Project.OpenBook.Dto.keyword.KeywordDto;

import Project.OpenBook.Dto.keyword.KeywordUserDto;
import Project.OpenBook.Dto.topic.TopicCustomerDto;
import Project.OpenBook.Dto.topic.TopicNumberDto;
import Project.OpenBook.Dto.topic.TopicUserDto;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.primarydate.PrimaryDateRepository;
import Project.OpenBook.Repository.sentence.SentenceRepository;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.topic.TopicAdminDto;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.dupdate.DupDateRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import com.querydsl.core.group.Group;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QPrimaryDate.primaryDate;
import static Project.OpenBook.Domain.QSentence.sentence;
import static Project.OpenBook.Domain.QTopic.topic;

@Service
@RequiredArgsConstructor
public class TopicService {

    private final TopicRepository topicRepository;
    private final CategoryRepository categoryRepository;
    private final ChapterRepository chapterRepository;

    private final DupDateRepository dupDateRepository;

    private final ChoiceRepository choiceRepository;

    private final DescriptionRepository descriptionRepository;

    private final KeywordRepository keywordRepository;
    private final CustomerRepository customerRepository;

    private final SentenceRepository  sentenceRepository;

    private final PrimaryDateRepository primaryDateRepository;
    private final TopicProgressRepository topicProgressRepository;

    public TopicAdminDto queryTopicAdmin(String topicTitle) {
        checkTopic(topicTitle);
        TopicAdminDto topicAdminDto = topicRepository.queryTopicAdminDto(topicTitle);
        return topicAdminDto;
    }

    public TopicUserDto queryTopicUser(Integer chapterNum, Integer topicNum) {
        return null;
    }

    public Topic createTopic(TopicAdminDto topicAdminDto) {

        Category category = checkCategory(topicAdminDto.getCategory());
        Chapter chapter = checkChapter(topicAdminDto.getChapter());
        checkDupTopicTitle(topicAdminDto.getTitle());

        //토픽 저장
        Topic topic = Topic.builder()
                .chapter(chapter)
                .category(category)
                .title(topicAdminDto.getTitle())
                .startDate(topicAdminDto.getStartDate())
                .endDate(topicAdminDto.getEndDate())
                .detail(topicAdminDto.getDetail())
                .startDateCheck(topicAdminDto.getStartDateCheck())
                .endDateCheck(topicAdminDto.getEndDateCheck())
                .questionNum(0)
                .choiceNum(0)
                .build();
        topicRepository.save(topic);

        //연표에 표시할 날짜 저장
        List<PrimaryDateDto> dateList = new ArrayList<>();
        if (topicAdminDto.getExtraDateList() != null) {
            dateList = topicAdminDto.getExtraDateList();
        }
        List<PrimaryDate> primaryDateList = dateList.stream().map(d -> new PrimaryDate(d.getExtraDate(), d.getExtraDateCheck(), d.getExtraDateComment(), topic))
                .collect(Collectors.toList());
        primaryDateRepository.saveAll(primaryDateList);

        //주제학습 레코드 생성
        updateTopicProgress(topic);

        return topic;
    }

    private void updateTopicProgress(Topic topic) {
        List<TopicProgress> topicProgressList = customerRepository.findAll().stream()
                .map(c -> new TopicProgress(c, topic))
                .collect(Collectors.toList());
        topicProgressRepository.saveAll(topicProgressList);
    }

    private void checkTopicNumber(Integer chapterNum, Integer topicNum) {
        topicRepository.queryTopicByNumber(chapterNum, topicNum)
                .ifPresent(t -> {
                    throw new CustomException(DUP_TOPIC_NUMBER);
                } );
    }

    public Topic updateTopic(String topicTitle, TopicAdminDto topicAdminDto) {
        Topic topic = checkTopic(topicTitle);
        String inputTitle = topicAdminDto.getTitle();

        if(!topicTitle.equals(inputTitle)){
            //새로 입력받은 제목이 중복되는지 확인
            checkDupTopicTitle(inputTitle);
        }
        String categoryName = topicAdminDto.getCategory();
        Category category = checkCategory(categoryName);

        int chapterNum = topicAdminDto.getChapter();
        Chapter chapter = checkChapter(chapterNum);

//        boolean flag = false;
//        if(topic.getStartDate()!=topicDto.getStartDate() || topic.getEndDate() != topicDto.getEndDate()){
//            flag = true;
//        }

        //토픽 수정
        topic.updateTopic(topicAdminDto.getTitle(), topicAdminDto.getStartDate(), topicAdminDto.getEndDate(), topicAdminDto.getStartDateCheck(),
                topicAdminDto.getEndDateCheck(), topicAdminDto.getDetail(), chapter, category);

        //연표에 나올 날짜 수정
        List<PrimaryDate> prevPrimaryDateList = primaryDateRepository.queryDatesByTopic(topic.getId());
        primaryDateRepository.deleteAllInBatch(prevPrimaryDateList);
        List<PrimaryDate> primaryDateList = topicAdminDto.getExtraDateList().stream()
                .map(d -> new PrimaryDate(d.getExtraDate(), d.getExtraDateCheck(), d.getExtraDateComment(), topic))
                .collect(Collectors.toList());
        primaryDateRepository.saveAll(primaryDateList);



        //시작날짜와 종료날짜가 변경된경우 보기와 정답이 될수있는지 다시 체크
//        if (flag) {
//            List<DupDate> dupDateList = dupDateRepository.queryAllByTopic(topic.getTitle());
//            dupDateRepository.deleteAllInBatch(dupDateList);
//            addDescriptionTopics(topic);
//            addAnswerTopics(topic);
//        }

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

        List<Keyword> keywordList = keywordRepository.queryKeywordsByTopic(topicTitle);
        if (!keywordList.isEmpty()) {
            throw new CustomException(TOPIC_HAS_KEYWORD);
        }

        List<Sentence> sentenceList = sentenceRepository.queryByTopicTitle(topicTitle);
        if (!sentenceList.isEmpty()) {
            throw new CustomException(TOPIC_HAS_SENTENCE);
        }

        List<PrimaryDate> primaryDateList = primaryDateRepository.queryDatesByTopic(topic.getId());
        primaryDateRepository.deleteAllInBatch(primaryDateList);

        topicRepository.delete(topic);

        return true;
    }

    private void addAnswerTopics(Topic topic) {
        List<Topic> topicList = dupDateRepository.queryAnswerTopics(topic.getStartDate(), topic.getEndDate());
        List<DupDate> dupDateList = new ArrayList<>();

        for (Topic descriptionTopic : topicList) {
            dupDateList.add(new DupDate(topic, descriptionTopic));
        }
        dupDateRepository.saveAll(dupDateList);
    }

    private void addDescriptionTopics(Topic topic) {
        if (topic.getStartDate() != null && topic.getEndDate() != null) {
            List<Topic> topicList = dupDateRepository.queryDescriptionTopics(topic.getStartDate(), topic.getEndDate());
            List<DupDate> dupDateList = new ArrayList<>();

            for (Topic descriptionTopic : topicList) {
                dupDateList.add(new DupDate(topic, descriptionTopic));
            }
            dupDateRepository.saveAll(dupDateList);
        }

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

    public List<KeywordDto> queryTopicKeywords(String topicTitle) {
        checkTopic(topicTitle);
        return keywordRepository.queryKeywordsByTopic(topicTitle).stream()
                .map(k -> new KeywordDto(k.getName(), k.getComment(), k.getImageUrl(), k.getId()))
                .collect(Collectors.toList());
    }


    public List<Sentence> queryTopicSentences(String topicTitle) {
        checkTopic(topicTitle);
        return sentenceRepository.queryByTopicTitle(topicTitle);
    }

    @Transactional(rollbackFor = {Exception.class})
    public void updateTopicNumber(List<TopicNumberDto> topicNumberDtoList) {
        for (TopicNumberDto topicNumberDto : topicNumberDtoList) {
            Topic topic = checkTopic(topicNumberDto.getTitle());
            topic.updateTopicNumber(topicNumberDto.getNumber());
        }
    }

    public TopicCustomerDto queryTopicCustomer(String topicTitle) {

        checkTopic(topicTitle);
        Map<String, Group> topicCustomerDtoMap = topicRepository.queryTopicCustomerDto(topicTitle);
        Group group = topicCustomerDtoMap.get(topicTitle);

        Integer startDate = group.getOne(topic.startDate);
        Integer endDate = group.getOne(topic.endDate);
        String category = group.getOne(topic.category.name);
        List<String> sentenceList = group.getList(sentence.name).stream()
                .distinct()
                .collect(Collectors.toList());
        Integer chapterNum = group.getOne(topic.chapter.number);
        List<KeywordUserDto> keywordList = group.getList(keyword).stream()
                .distinct()
                .map(k -> new KeywordUserDto(k.getName(), k.getComment(), k.getImageUrl()))
                .collect(Collectors.toList());
        List<PrimaryDateUserDto> extraDateList = group.getList(primaryDate).stream()
                .distinct()
                .map(p -> new PrimaryDateUserDto(p.getExtraDate(), p.getExtraDateComment()))
                .collect(Collectors.toList());

        return new TopicCustomerDto(category, startDate, endDate, extraDateList, keywordList, sentenceList);
    }
}
