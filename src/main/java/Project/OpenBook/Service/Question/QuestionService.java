package Project.OpenBook.Service.Question;

import Project.OpenBook.Dto.keyword.KeywordNameCommentDto;
import Project.OpenBook.Dto.question.*;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.sentence.SentenceRepository;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Constants.QuestionConst.*;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QSentence.sentence;

@Service
//@RequiredArgsConstructor
public class QuestionService {

    private TopicRepository topicRepository;
    private ChapterRepository chapterRepository;
    private KeywordRepository keywordRepository;
    private SentenceRepository sentenceRepository;

    private GetKeywordByTopicQuestion type1 ;
    private GetSentenceByTopicQuestion type2;
    private GetTopicByKeywordQuestion type3;
    private GetTopicBySentenceQuestion type4;

    public QuestionService(TopicRepository topicRepository, ChapterRepository chapterRepository, KeywordRepository keywordRepository, SentenceRepository sentenceRepository) {
        this.topicRepository = topicRepository;
        this.chapterRepository = chapterRepository;
        this.keywordRepository = keywordRepository;
        this.sentenceRepository = sentenceRepository;

        this.type1 = new GetKeywordByTopicQuestion(this.topicRepository, this.keywordRepository, this.sentenceRepository);
        this.type2 = new GetSentenceByTopicQuestion(this.topicRepository, this.keywordRepository, this.sentenceRepository);
        this.type3 = new GetTopicByKeywordQuestion(this.topicRepository, this.keywordRepository, this.sentenceRepository);
        this.type4 = new GetTopicBySentenceQuestion(this.topicRepository, this.keywordRepository, this.sentenceRepository);
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    private Chapter checkChapter(Integer num) {
        return chapterRepository.findOneByNumber(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });
    }

    private Integer queryRandChapterNum() {
        Integer maxNum = chapterRepository.queryMaxChapterNum();
        Random random = new Random();
        return random.nextInt(maxNum) + 1;
    }

    public List<TimeFlowQuestionDto> queryTimeFlowQuestion(Integer num) {
        Map<Topic, List<PrimaryDate>> m = new HashMap<>();
        if (num == 0) {
            m = topicRepository.queryTimeFlowQuestion();
        } else {
            if (num == -1) {
                num = queryRandChapterNum();
            }
            m = topicRepository.queryTimeFlowQuestion(num);
        }

        List<TimeFlowQuestionDto> timeFlowQuestionDtoList = new ArrayList<>();
        for (Topic topic : m.keySet()) {
            String topicTitle = topic.getTitle();
            if (topic.getStartDateCheck()) {
                timeFlowQuestionDtoList.add(new TimeFlowQuestionDto(topic.getStartDate(), makeComment(topicTitle, "startDate"), topicTitle));
            }
            if (topic.getEndDateCheck()) {
                timeFlowQuestionDtoList.add(new TimeFlowQuestionDto(topic.getEndDate(), makeComment(topicTitle, "endDate"), topicTitle));
            }
            List<PrimaryDate> primaryDateList = m.get(topic);
            for (PrimaryDate primaryDate : primaryDateList) {
                if (primaryDate.getExtraDateCheck()) {
                    timeFlowQuestionDtoList.add(new TimeFlowQuestionDto(primaryDate.getExtraDate(), primaryDate.getExtraDateComment(), topicTitle));
                }
            }
        }

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

    @Transactional
    public List<QuestionDto> queryGetSentencesQuestion(String topicTitle) {
        Topic topic = checkTopic(topicTitle);

        return type2.getJJHQuestion(topicTitle);
    }

    public List<QuestionDto> queryGetTopicsByKeywordQuestion(Integer num) {
        List<QuestionDto> questionList = new ArrayList<>();


        Chapter chapter = chapterRepository.queryChapterWithTopic(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });

        List<String> topicTitleList = chapter.getTopicList().stream()
                .map(Topic::getTitle)
                .collect(Collectors.toList());
        for (String topicTitle : topicTitleList) {
            QuestionDto dto = type3.getQuestion(topicTitle);
            //TODO : 문제가 만들어지지 않았을때 예외처리
            questionList.add(dto);
        }

        return questionList;
    }

    public List<QuestionDto> queryGetTopicsBySentenceQuestion(Integer num) {
        List<QuestionDto> questionList = new ArrayList<>();

        Chapter chapter = chapterRepository.queryChapterWithTopic(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });

        List<String> topicTitleList = chapter.getTopicList().stream()
                .map(Topic::getTitle)
                .collect(Collectors.toList());
        for (String topicTitle : topicTitleList) {
            QuestionDto dto = type4.getQuestion(topicTitle);
            //TODO : 문제가 만들어지지 않았을때 예외처리
            questionList.add(dto);
        }
        return questionList;
    }

    public List<QuestionDto> queryRandomQuestion(Integer chapterNum, Integer questionCount) {
        List<String> topicTitleList = new ArrayList<>();
        List<QuestionDto> questionList = new ArrayList<>();
        if (chapterNum == 0) {
            topicTitleList = topicRepository.findAll().stream()
                    .map(Topic::getTitle)
                    .collect(Collectors.toList());
        } else {
            Chapter chapter = chapterRepository.queryChapterWithTopic(chapterNum).orElseThrow(() -> {
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
            if (i % 4 == 0) {
                //1유형
                dto = type1.getQuestion(title);
            } else if (i % 4 == 1) {
                //2유형
                dto = type2.getQuestion(title);
            } else if (i % 4 == 2) {
                //3유형
                dto = type3.getQuestion(title);
            } else if (i % 4 == 3) {
                //4유형
                dto = type4.getQuestion(title);
            }

            questionList.add(dto);
        }

        return questionList;
    }
}

