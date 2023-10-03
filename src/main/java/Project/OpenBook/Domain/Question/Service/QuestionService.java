package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Question.Dto.TimeFlowQuestionDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Handler.Exception.CustomException;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
//@RequiredArgsConstructor
public class QuestionService {

    private TopicRepository topicRepository;
    private ChapterRepository chapterRepository;
    private KeywordRepository keywordRepository;

    private GetKeywordByTopicQuestion type1 ;
//    private GetSentenceByTopicQuestion type2;
    private GetTopicByKeywordQuestion type3;
//    private GetTopicBySentenceQuestion type4;

    public QuestionService(TopicRepository topicRepository, ChapterRepository chapterRepository, KeywordRepository keywordRepository) {
        this.topicRepository = topicRepository;
        this.chapterRepository = chapterRepository;
        this.keywordRepository = keywordRepository;

        this.type1 = new GetKeywordByTopicQuestion(this.topicRepository, this.keywordRepository);
//        this.type2 = new GetSentenceByTopicQuestion(this.topicRepository, this.keywordRepository, this.sentenceRepository);
        this.type3 = new GetTopicByKeywordQuestion(this.topicRepository, this.keywordRepository);
//        this.type4 = new GetTopicBySentenceQuestion(this.topicRepository, this.keywordRepository, this.sentenceRepository);
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    private Integer queryRandChapterNum() {
        Integer maxNum = chapterRepository.queryMaxChapterNum().orElseGet( () -> 0);
        Random random = new Random();
        return random.nextInt(maxNum) + 1;
    }

    @Transactional
    public List<TimeFlowQuestionDto> queryTimeFlowQuestion(Integer num) {
        List<Topic> topicList = new ArrayList<>();
        if (num == 0) {
            topicList = topicRepository.findAll();
        }else{
            if (num == -1) {
                num = queryRandChapterNum();
            }
            Chapter chapter = chapterRepository.findOneByNumber(num).orElseThrow(() -> {
                throw new CustomException(CHAPTER_NOT_FOUND);
            });
            topicList = chapter.getTopicList();
        }

        List<TimeFlowQuestionDto> timeFlowQuestionDtoList = new ArrayList<>();
//        for (Topic topic : topicList) {
//            String topicTitle = topic.getTitle();
//            if (topic.getStartDateCheck()) {
//                timeFlowQuestionDtoList.add(new TimeFlowQuestionDto(topic.getStartDate(), makeComment(topicTitle, "startDate"), topicTitle));
//            }
//            if (topic.getEndDateCheck()) {
//                timeFlowQuestionDtoList.add(new TimeFlowQuestionDto(topic.getEndDate(), makeComment(topicTitle, "endDate"), topicTitle));
//            }
//            List<TopicPrimaryDate> topicPrimaryDateList = topic.getTopicPrimaryDateList();
//            for (TopicPrimaryDate topicPrimaryDate : topicPrimaryDateList) {
//                if (topicPrimaryDate.getExtraDateCheck()) {
//                    timeFlowQuestionDtoList.add(new TimeFlowQuestionDto(topicPrimaryDate.getExtraDate(), topicPrimaryDate.getExtraDateComment(), topicTitle));
//                }
//            }
//        }

        //연도 순으로 오름차순으로 정렬
        Collections.sort(timeFlowQuestionDtoList, Comparator.comparing(TimeFlowQuestionDto::getDate));

        return timeFlowQuestionDtoList;
    }

    public String makeComment(String topicTitle, String type) {
        if (type.equals("startDate")) {
            return topicTitle + "의 시작연도입니다.";
        } else {
            return topicTitle + "의 종료연도입니다.";
        }
    }

    @Transactional
    public List<QuestionDto> queryGetKeywordsQuestion(String topicTitle) {
        Topic topic = checkTopic(topicTitle);

        return type1.getJJHQuestion(topicTitle);
    }

//    @Transactional
//    public List<QuestionDto> queryGetSentencesQuestion(String topicTitle) {
//        Topic topic = checkTopic(topicTitle);
//
//        return type2.getJJHQuestion(topicTitle);
//    }

    @Transactional
    public List<QuestionDto> queryGetTopicsByKeywordQuestion(Integer num) {
        List<QuestionDto> questionList = new ArrayList<>();


        Chapter chapter = chapterRepository.findOneByNumber(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });

        List<String> topicTitleList = chapter.getTopicList().stream()
                .map(Topic::getTitle)
                .collect(Collectors.toList());
        for (String topicTitle : topicTitleList) {
            QuestionDto dto = type3.getQuestion(topicTitle);
            if (dto != null) {
                questionList.add(dto);
            }
        }

        return questionList;
    }

//    @Transactional
//    public List<QuestionDto> queryGetTopicsBySentenceQuestion(Integer num) {
//        List<QuestionDto> questionList = new ArrayList<>();
//
//        Chapter chapter = chapterRepository.findOneByNumber(num).orElseThrow(() -> {
//            throw new CustomException(CHAPTER_NOT_FOUND);
//        });
//
//        List<String> topicTitleList = chapter.getTopicList().stream()
//                .map(Topic::getTitle)
//                .collect(Collectors.toList());
//        for (String topicTitle : topicTitleList) {
//            QuestionDto dto = type4.getQuestion(topicTitle);
//            if (dto != null) {
//                questionList.add(dto);
//            }
//        }
//        return questionList;
//    }

    @Transactional
    public List<QuestionDto> queryRandomQuestion(Integer chapterNum, Integer questionCount) {
        List<String> topicTitleList = new ArrayList<>();
        List<QuestionDto> questionList = new ArrayList<>();
        if (chapterNum == 0) {
            topicTitleList = topicRepository.findAll().stream()
                    .map(Topic::getTitle)
                    .collect(Collectors.toList());
        } else {
            Chapter chapter = chapterRepository.findOneByNumber(chapterNum).orElseThrow(() -> {
                throw new CustomException(CHAPTER_NOT_FOUND);
            });

            topicTitleList = chapter.getTopicList().stream()
                    .map(Topic::getTitle)
                    .collect(Collectors.toList());
        }
        int totalSize = topicTitleList.size();

        for (int i = 0; i < questionCount; i++) {
            String title = topicTitleList.get(i % totalSize);
            QuestionDto dto = null;
            if (i % 2 == 0) {
                //1유형
                dto = type1.getQuestion(title);
            } else if (i % 2 == 1) {
                //2유형
                dto = type3.getQuestion(title);
            }

            questionList.add(dto);
        }

        return questionList;
    }
}

