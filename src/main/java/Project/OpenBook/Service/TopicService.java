package Project.OpenBook.Service;

import Project.OpenBook.Dto.PrimaryDateDto;
import Project.OpenBook.Dto.keyword.KeywordDto;

import Project.OpenBook.Repository.primarydate.PrimaryDateRepository;
import Project.OpenBook.Repository.imagefile.ImageFileRepository;
import Project.OpenBook.Repository.sentence.SentenceRepository;
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
    private final ImageFileRepository imageFileRepository;

    private final SentenceRepository  sentenceRepository;

    private final PrimaryDateRepository primaryDateRepository;

    public TopicDto queryTopic(String topicTitle) {
        checkTopic(topicTitle);
        TopicDto topicDto = topicRepository.queryTopicDto(topicTitle);
        return topicDto;
    }

    public Topic createTopic(TopicDto topicDto) {

        Category category = checkCategory(topicDto.getCategory());
        Chapter chapter = checkChapter(topicDto.getChapter());
        checkDupTopicTitle(topicDto.getTitle());

        //토픽 저장
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

        //연표에 표시할 날짜 저장
        List<PrimaryDateDto> dateList = topicDto.getDateList();
        List<PrimaryDate> primaryDateList = dateList.stream().map(d -> new PrimaryDate(d.getDate(), d.getDateCheck(), d.getDateComment(), topic))
                .collect(Collectors.toList());
        primaryDateRepository.saveAll(primaryDateList);

        //정답과 보기가 될수 있는 토픽인지 확인후 저장
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

        //토픽 수정
        topic.updateTopic(topicDto.getTitle(), topicDto.getStartDate(),topicDto.getEndDate(), topicDto.getDetail(),
                chapter, category);

        //연표에 나올 날짜 수정
        List<PrimaryDate> prevPrimaryDateList = primaryDateRepository.queryDatesByTopic(topicTitle);
        primaryDateRepository.deleteAllInBatch(prevPrimaryDateList);
        List<PrimaryDate> primaryDateList = topicDto.getDateList().stream()
                .map(d -> new PrimaryDate(d.getDate(), d.getDateCheck(), d.getDateComment(), topic))
                .collect(Collectors.toList());
        primaryDateRepository.saveAll(primaryDateList);



        //시작날짜와 종료날짜가 변경된경우 보기와 정답이 될수있는지 다시 체크
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

        List<Keyword> keywordList = keywordRepository.queryKeywordsByTopic(topicTitle);
        if (!keywordList.isEmpty()) {
            throw new CustomException(TOPIC_HAS_KEYWORD);
        }

        //연표에 나올 중요 연도들 삭제
        List<PrimaryDate> primaryDateList = primaryDateRepository.queryDatesByTopic(topicTitle);
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

    public List<KeywordDto> queryTopicKeywords(String topicTitle) {
        checkTopic(topicTitle);
        List<Keyword> keywordList = keywordRepository.queryKeywordsByTopic(topicTitle);
        List<KeywordDto> keywordDtoList = new ArrayList<>();

        for (Keyword keyword : keywordList) {
            String name = keyword.getName();
            String comment = keyword.getComment();
            Long id = keyword.getId();

            List<ImageFile> imageFiles = imageFileRepository.queryByKeyword(id);
            List<String> imageUrlList = new ArrayList<>();
            for (ImageFile imageFile : imageFiles) {
                Long imgId = imageFile.getId();
                String url = "http://localhost:8080/images/" + imgId;
                imageUrlList.add(url);
            }

            KeywordDto keywordDto = new KeywordDto(name, comment, imageUrlList, id);
            keywordDtoList.add(keywordDto);
        }
        return keywordDtoList;
    }


    public List<Sentence> queryTopicSentences(String topicTitle) {
        checkTopic(topicTitle);
        return sentenceRepository.queryByTopicTitle(topicTitle);
    }
}
