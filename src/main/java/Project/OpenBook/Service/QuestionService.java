package Project.OpenBook.Service;

import Project.OpenBook.Constants.QuestionConst;
import Project.OpenBook.Dto.Sentence.SentenceWithTopicDto;
import Project.OpenBook.Dto.keyword.KeywordNameCommentDto;
import Project.OpenBook.Dto.keyword.KeywordWithTopicDto;
import Project.OpenBook.Dto.question.*;
import Project.OpenBook.Repository.QuestionChoiceRepository;
import Project.OpenBook.Repository.QuestionDescriptionRepository;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.sentence.SentenceRepository;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.choice.ChoiceContentIdDto;
import Project.OpenBook.Dto.description.DescriptionContentIdDto;
import Project.OpenBook.Repository.category.CategoryRepository;
import Project.OpenBook.Repository.dupdate.DupDateRepository;
import Project.OpenBook.Repository.question.QuestionRepository;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import com.querydsl.core.Tuple;
import io.swagger.models.auth.In;
import lombok.RequiredArgsConstructor;

import org.springframework.core.env.Environment;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Constants.QuestionConst.*;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QPrimaryDate.*;
import static Project.OpenBook.Domain.QPrimaryDate.primaryDate;
import static Project.OpenBook.Domain.QSentence.sentence;
import static Project.OpenBook.Domain.QTopic.*;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final TopicRepository topicRepository;

    private final QuestionRepository questionRepository;

    private final CategoryRepository categoryRepository;

    private final ChapterRepository chapterRepository;

    private final KeywordRepository keywordRepository;
    private final SentenceRepository sentenceRepository;


//    @Transactional
//    public QuestionDto makeQuestionTimeAndDescription(Long type, String categoryName, String topicTitle) {
//
//        checkCategory(categoryName);
//        Topic answerTopic = null;
//        //정답 토픽 선정
//        if (topicTitle != null) {
//            answerTopic = checkTopic(topicTitle);
//        }else{
//            answerTopic = topicRepository.queryRandTopicByCategory(categoryName);
//        }
//
//        TempQ tempQ = null;
//        if (type == 1) {
//            tempQ = makeDescriptionQuestion(answerTopic);
//        } else if (type == 2) {
//            tempQ = makeTimeType2Question();
//        } else if (type > 2 && type <= 4) {
//            tempQ = makeTimeType34Question(answerTopic, type);
//        } else if (type == 5) {
//            tempQ = makeTimeFlowQuestion(type);
//        }
//
//        if (tempQ.choiceList.size() != 5) {
//            throw new CustomException(NOT_ENOUGH_CHOICE);
//        }
//
//        List<ChoiceContentIdDto> choiceList = new ArrayList<>();
//        Long answerId = null;
//        DescriptionContentIdDto descriptionContentIdDto = new DescriptionContentIdDto(tempQ.description.getId(), tempQ.description.getContent());
//
//        if(type >= 1 && type <= 4){
//            choiceList = tempQ.choiceList.stream().map(c -> new ChoiceContentIdDto(c.getContent(), c.getId())).collect(Collectors.toList());
//            answerId = choiceList.get(choiceNum-1).getId();
//        } else if (type == 5) {
//            int index = tempQ.choiceList.indexOf(null);
//            answerId = Integer.toUnsignedLong(index);
//            choiceList.subList(index, index + 1).clear();
//        }
//
//        return QuestionDto.builder()
//                .categoryName(categoryName)
//                .answerChoiceId(answerId)
//                .prompt(tempQ.prompt)
//                .type(type)
//                .description(descriptionContentIdDto)
//                .choiceList(choiceList)
//                .build();
//    }
//
//
//    //특정 주제에 대한 설명으로 옳은것을 찾는 문제생성 메서드
//    private TempQ makeDescriptionQuestion(Topic answerTopic) {
//
//        String title = answerTopic.getTitle();
//        String categoryName = answerTopic.getCategory().getName();
//
//        //문제 생성
//        String prefix = env.getProperty("description.prefix",String.class);
//        String suffix = env.getProperty("description.suffix",String.class);
//        String prompt = prefix + " " +  categoryName + suffix;
//
//        //정답 주제에 대한 보기와 선지를 가져옴
//        Description description = descriptionRepository.findRandDescriptionByTopic(title);
//        Choice answerChoice = choiceRepository.queryRandChoiceByTopic(answerTopic.getId(),description.getId());
//
//        //정답 주제와 같은 카테고리를 가진 나머지 주제들에서 선지를 가져옴
//        List<Choice> choiceList = choiceRepository.queryRandChoicesByCategory(title, categoryName, choiceNum-1);
//        choiceList.add(answerChoice);
//
//        return new TempQ(prompt, description, choiceList);
//    }
//
//    private TempQ makeTimeType34Question(Topic answerTopic, Long type) {
//        String title = answerTopic.getTitle();
//        String categoryName = answerTopic.getCategory().getName();
//        Integer startDate = answerTopic.getStartDate();
//        Integer endDate = answerTopic.getEndDate();
//
//        String prompt = setPrompt(categoryName, type);
//
//        Description description = descriptionRepository.findRandDescriptionByTopic(title);
//
//        List<Choice> choiceList = new ArrayList<>();
//        if (type == 3) {
//            //보기에서 주어진 사건보다 나중에 발생한 사건 찾는 문제
//            choiceList = choiceRepository.queryChoicesType3(title, startDate, endDate,choiceNum, 0, categoryName);
//        } else if (type == 4) {
//            //보기에 주어진 사건보다 이전에 발생한 사건 찾는 문제
//            choiceList = choiceRepository.queryChoicesType4(title, startDate,endDate, choiceNum, 0, categoryName);
//        }
//
//        return new TempQ(prompt, description, choiceList);
//    }
//
//    private TempQ makeTimeType2Question() {
//
//        Topic answerTopic = dupDateRepository.queryRandomAnswerTopic();
//        if (answerTopic == null) {
//            throw new CustomException(QUESTION_ERROR);
//        }
//        Topic descriptionTopic = dupDateRepository.queryRandomDescriptionTopic(answerTopic);
//
//        String categoryName = answerTopic.getCategory().getName();
//
//        String prompt = setPrompt(categoryName, 2L);
//
//        Description description = descriptionRepository.findRandDescriptionByTopic(descriptionTopic.getTitle());
//
//        List<Choice> choiceList = choiceRepository.queryChoicesType2(answerTopic, descriptionTopic, choiceNum, 0, categoryName);
//
//        return new TempQ(prompt, description, choiceList);
//    }
//
//
//
//    public TempQ makeTimeFlowQuestion(Long type) {
//        String prompt = setPrompt("사건", type);
//
//        List<Choice> choiceList = choiceRepository.queryRandChoicesByCategory("사건", 7);
//        Random rand = new Random();
//        int answerNum = rand.nextInt(5)+1;
//        Choice answerChoice = choiceList.get(answerNum);
//        Description description = descriptionRepository.findRandDescriptionByTopic(answerChoice.getTopic().getTitle());
//        choiceList.set(answerNum, null);
//
//        return new TempQ(prompt, description, choiceList);
//    }
//
//    @Transactional
//    public Question addQuestion(QuestionDto questionDto){
//
//        Long type = questionDto.getType();
//        String categoryName = questionDto.getCategoryName();
//        Category category = checkCategory(categoryName);
//
//        List<Long> choiceIdList = questionDto.getChoiceList().stream().map(c -> c.getId()).collect(Collectors.toList());
//        List<Choice> choiceList = choiceRepository.queryChoicesById(choiceIdList);
//        Long descriptionId = questionDto.getDescription().getId();
//
//        if (type > 5) {
//            throw new CustomException(INVALID_PARAMETER);
//        }
//
//        //문제 저장
//        Question question = Question.builder()
//                .answerChoiceId(questionDto.getAnswerChoiceId())
//                .prompt(questionDto.getPrompt())
//                .type(type)
//                .category(category)
//                .build();
//        questionRepository.save(question);
//
//        //선지 저장
//        List<QuestionChoice> questionChoiceList = new ArrayList<>();
//        for (Choice choice : choiceList) {
//            questionChoiceList.add(new QuestionChoice(question, choice));
//        }
//        questionChoiceRepository.saveAll(questionChoiceList);
//
//        //보기 저장
//        Description description = descriptionRepository.findById(descriptionId).orElseThrow(() -> {
//            throw new CustomException(DESCRIPTION_NOT_FOUND);
//        });
//        questionDescriptionRepository.save(new QuestionDescription(question, description));
//
//        return question;
//    }
//
//    @Transactional
//    public Question updateQuestion(Long questionId, QuestionDto questionDto) {
//
//        Question question = checkQuestion(questionId);
//
//        Long type = questionDto.getType();
//        String categoryName = questionDto.getCategoryName();
//        Category category = checkCategory(categoryName);
//
//        if (type > 5) {
//            throw new CustomException(INVALID_PARAMETER);
//        }
//
//        Question updatedQuestion = question.updateQuestion(questionDto.getPrompt(), questionDto.getAnswerChoiceId(), type, category);
//
//        return updatedQuestion;
//    }
//
//    @Transactional
//    public QuestionDto queryQuestion(Long id) {
//        return questionRepository.findQuestionById(id);
//    }
//
//    public boolean deleteQuestion(Long questionId) {
//
//        checkQuestion(questionId);
//        questionRepository.deleteById(questionId);
//        return true;
//    }
//
//    public void queryTimeFlowQuestion(Integer num) {
//    }

//    private class TempQ {
//        private String prompt;
//        private Description description;
//        private List<Choice> choiceList;
//
//        public TempQ(String prompt, Description description, List<Choice> choiceList) {
//            this.prompt = prompt;
//            this.description = description;
//            this.choiceList = choiceList;
//        }
//    }

//    private String setPrompt(String categoryName, Long type) {
//        String prompt = null;
//        if(categoryName.equals("사건")){
//            if (type == 2) {
//                prompt = env.getProperty("time.case.between", String.class);
//            } else if (type == 3) {
//                prompt = env.getProperty("time.case.after", String.class);
//            } else if (type == 4) {
//                prompt = env.getProperty("time.case.before", String.class);
//            } else if (type == 5) {
//                prompt = env.getProperty("timeFlow.case", String.class);
//            }
//        }
//        //나중에 다른 일에 대한 문제 생성시 추가
//        return prompt;
//    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    private Category checkCategory(String categoryName) {
        return categoryRepository.findCategoryByName(categoryName).orElseThrow(() ->{
            throw new CustomException(CATEGORY_NOT_FOUND);
        });
    }

    private Question checkQuestion(Long questionId) {
        return questionRepository.findById(questionId).orElseThrow(() ->{
            throw new CustomException(QUESTION_NOT_FOUND);
        });
    }

    private Chapter checkChapter(Integer num) {
        return chapterRepository.findOneByNumber(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });
    }


    public List<TimeFlowQuestionDto> queryTimeFlowQuestion(Integer num) {
        Chapter chapter = checkChapter(num);

        List<Tuple> tuples = topicRepository.queryTimeFlowQuestion(num);

        /**
         * <topic, List<PrimaryDate> map 생성 로직
         * (topic과 primaryDate는 1:N관계)
         */
        Map<Topic, List<PrimaryDate>> m =new HashMap<>();
        for (Tuple t : tuples) {
            Topic topic = t.get(QTopic.topic);
            PrimaryDate primaryDate = t.get(QPrimaryDate.primaryDate);
            if (primaryDate != null) {
                m.computeIfAbsent(topic, k -> new ArrayList<>()).add(primaryDate);
            } else{
                m.computeIfAbsent(topic, k -> new ArrayList<>());
            }
        }

        List<TimeFlowQuestionDto> timeFlowQuestionDtoList = new ArrayList<>();
        for (Topic topic  : m.keySet()) {
            String topicTitle = topic.getTitle();
            if (topic.getStartDateCheck()) {
                timeFlowQuestionDtoList.add(new TimeFlowQuestionDto(topic.getStartDate(), makeComment(topicTitle, "startDate"),topicTitle));
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
        Collections.sort(timeFlowQuestionDtoList, Comparator.comparing(dto -> dto.getDate()));

        return timeFlowQuestionDtoList;
    }

    public String makeComment(String topicTitle, String type) {
        if(type.equals("startDate")){
            return topicTitle + "의 시작연도입니다.";
        } else {
            return topicTitle + "의 종료연도입니다.";
        }

    }

    @Transactional
    public GetKeywordQuestionDto queryGetKeywordsQuestion(String topicTitle) {
        Topic topic = checkTopic(topicTitle);

        //정답 키워드, 문장 조회
        List<Keyword> answerKeywordList = keywordRepository.queryKeywordsByTopic(topicTitle);
        int answerKeywordSize = answerKeywordList.size();
        List<Sentence> answerSentenceList = sentenceRepository.queryByTopicTitle(topicTitle);
        int answerSentenceSize = answerSentenceList.size();

        //오답 키워드, 문장 조회
        List<Tuple> wrongKeywordRet = keywordRepository.queryWrongKeywords(topicTitle, answerKeywordSize * WRONG_ANSWER_NUM);
        List<Tuple> wrongSentenceRet = sentenceRepository.queryWrongSentences(topicTitle, answerSentenceSize * WRONG_ANSWER_NUM);

        //return할 dto생성
        List<KeywordNameCommentDto> answerKeywordDtoList = answerKeywordList.stream().map(k -> new KeywordNameCommentDto(k.getName(), k.getComment())).collect(Collectors.toList());
        List<String> answerSentenceDtoList = answerSentenceList.stream().map(s -> s.getName()).collect(Collectors.toList());
        List<KeywordWithTopicDto> wrongKeywordDtoList = wrongKeywordRet.stream()
                .map(k -> new KeywordWithTopicDto(k.get(keyword.name), k.get(keyword.comment), k.get(keyword.topic.title))).collect(Collectors.toList());
        List<SentenceWithTopicDto> wrongSentenceDtoList = wrongSentenceRet.stream()
                .map(s -> new SentenceWithTopicDto(s.get(sentence.name), s.get(sentence.topic.title))).collect(Collectors.toList());

        return new GetKeywordQuestionDto(new GetKeywordAnswerDto(answerKeywordDtoList, answerSentenceDtoList),
                new GetKeywordWrongAnswerDto(wrongKeywordDtoList, wrongSentenceDtoList));
    }

    public void queryGetTopicsQuestion(Integer num) {
        Chapter chapter = checkChapter(num);


    }
}
