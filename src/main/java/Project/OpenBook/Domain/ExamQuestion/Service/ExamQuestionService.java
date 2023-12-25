package Project.OpenBook.Domain.ExamQuestion.Service;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeyword;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeywordRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Description.Repository.DescriptionRepository;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeyword;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeywordRepository;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.*;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository.ExamQuestionLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecord;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecordRepository;
import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Image.ImageService;
import Project.OpenBook.Redis.RedisService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ExamQuestionService {
    private final ExamQuestionRepository examQuestionRepository;
    private final RoundRepository roundRepository;
    private final RoundLearningRecordRepository roundLearningRecordRepository;
    private final TopicRepository topicRepository;
    private final DescriptionRepository descriptionRepository;
    private final ChoiceRepository choiceRepository;
    private final ChoiceKeywordRepository choiceKeywordRepository;
    private final DescriptionKeywordRepository descriptionKeywordRepository;
    private final ExamQuestionLearningRecordRepository examQuestionLearningRecordRepository;
    private final ImageService imageService;
    private final RedisService redisService;

    @Transactional(readOnly = true)
    public ExamQuestionInfoDto getExamQuestionInfo(Integer roundNumber, Integer questionNumber) {

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestion(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        ChoiceType choiceType = examQuestion.getChoiceType();

        return new ExamQuestionInfoDto(examQuestion.getNumber(), examQuestion.getAnswer(), choiceType.name(), examQuestion.getScore());
    }

    @Transactional(readOnly = true)
    public List<ExamQuestionDto> getRoundQuestions(Customer customer, Integer roundNumber) throws JsonProcessingException {
        checkRound(roundNumber);

//        try {
//            List<ExamQuestionDto> questionListFromRedis = redisService.getQuestionListFromRedis(roundNumber);
//        } catch (JsonProcessingException e) {
//            //해당 값 삭제 로직
//        }


        List<ExamQuestion> examQuestionList = examQuestionRepository.queryExamQuestionsForExamQuestionList(roundNumber);
        Map<Description, List<DescriptionKeyword>> descriptionKeywordMap = descriptionKeywordRepository.queryDescriptionKeywordForExamQuestion(roundNumber).stream()
                .collect(Collectors.groupingBy(DescriptionKeyword::getDescription));
        Map<Choice, List<ChoiceKeyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForExamQuestion(roundNumber).stream()
                .collect(Collectors.groupingBy(ChoiceKeyword::getChoice));
        Map<ExamQuestion, ExamQuestionLearningRecord> answerNoteMap = examQuestionLearningRecordRepository.queryExamQuestionLearningRecords(roundNumber, customer).stream()
                .collect(Collectors.toMap(ExamQuestionLearningRecord::getExamQuestion, an -> an));

        return getExamQuestionDtoList(examQuestionList, descriptionKeywordMap, choiceKeywordMap, answerNoteMap);
    }

    private List<AnswerNotedTopicQueryDto> makeAnswerNotedTopicQueryDtoList(List<ExamQuestion> examQuestionList, Map<Description, List<DescriptionKeyword>> descriptionKeywordMap, Map<Choice, List<ChoiceKeyword>> choiceKeywordMap, Map<ExamQuestion, ExamQuestionLearningRecord> answerNoteMap) {
        List<AnswerNotedTopicQueryDto> dtoList = new ArrayList<>();
        Map<Integer, List<AnswerNotedQuestionInfoDto>> questionInfoListMap = new HashMap<>();

        for (ExamQuestion question : examQuestionList) {
            Integer roundNumber = question.getRound().getNumber();
            Integer answerNumber = question.getAnswer();
            //보기 -> 보기 키워드 구성
            Description description = question.getDescription();
            List<DescriptionKeyword> descriptionKeywordList = descriptionKeywordMap.get(description);
            List<ExamQuestionCommentDto> descriptionCommentList = new ArrayList<>();
            if (descriptionKeywordList != null) {
                descriptionCommentList = makeDescriptionCommentList(descriptionKeywordList);
            }


            //선지 -> 선지 키워드 구성
            List<Choice> choiceList = question.getChoiceList();
            List<ExamQuestionCommentListDto> correctCommentList = new ArrayList<>();
            List<ExamQuestionCommentListDto> wrongCommentList = new ArrayList<>();
            for (Choice choice : choiceList) {
                List<ChoiceKeyword> choiceKeywordList = choiceKeywordMap.get(choice);
                if (choiceKeywordList != null) {
                    List<Keyword> keywordList = choiceKeywordList.stream()
                            .map(ck -> ck.getKeyword())
                            .collect(Collectors.toList());
                    List<ExamQuestionCommentDto> commentList = makeCommentList(keywordList);
                    ExamQuestionCommentListDto commentListDto = new ExamQuestionCommentListDto(commentList);
                    if (answerNumber == choice.getNumber()) {
                        correctCommentList.add(commentListDto);
                    }else{
                        wrongCommentList.add(commentListDto);
                    }
                }
            }

            //전체 ExamQuestionDto 구성
            AnswerNotedQuestionInfoDto dto = new AnswerNotedQuestionInfoDto(question, descriptionCommentList, correctCommentList, wrongCommentList);
            List<AnswerNotedQuestionInfoDto> questionInfoList = questionInfoListMap.get(roundNumber);
            if (questionInfoList == null) {
                questionInfoList = new ArrayList<>();
            }

            questionInfoList.add(dto);
            questionInfoListMap.put(roundNumber, questionInfoList);

        }

        for (Integer roundNumber : questionInfoListMap.keySet()) {
            dtoList.add(new AnswerNotedTopicQueryDto(roundNumber, questionInfoListMap.get(roundNumber)));
        }
        return  dtoList;
    }


    private List<ExamQuestionDto> getExamQuestionDtoList(List<ExamQuestion> examQuestionList, Map<Description, List<DescriptionKeyword>> descriptionKeywordMap, Map<Choice, List<ChoiceKeyword>> choiceKeywordMap, Map<ExamQuestion, ExamQuestionLearningRecord> answerNoteMap) {
        List<ExamQuestionDto> examQuestionDtoList = new ArrayList<>();

        for (ExamQuestion question : examQuestionList) {
            //보기 -> 보기 키워드 구성
            Description description = question.getDescription();
            List<DescriptionKeyword> descriptionKeywordList = descriptionKeywordMap.get(description);
            List<ExamQuestionCommentDto> descriptionCommentList = new ArrayList<>();
            if (descriptionKeywordList != null) {
                descriptionCommentList = makeDescriptionCommentList(descriptionKeywordList);
            }


            //선지 -> 선지 키워드 구성
            List<Choice> choiceList = question.getChoiceList();
            List<QuestionChoiceDto> questionChoiceDtoList = new ArrayList<>();
            for (Choice choice : choiceList) {
                List<ChoiceKeyword> choiceKeywordList = choiceKeywordMap.get(choice);
                QuestionChoiceDto dto = new QuestionChoiceDto(choice.getContent(), choice.getNumber(), new ArrayList<>());
                if (choiceKeywordList != null) {
                    dto = makeQuestionChoiceDto(choice, choiceKeywordList);
                }
                questionChoiceDtoList.add(dto);
            }

            //오답노트 구성
            ExamQuestionLearningRecord examQuestionLearningRecord = answerNoteMap.get(question);
            Boolean savedAnswerNote = examQuestionLearningRecord.getAnswerNoted();

            //전체 ExamQuestionDto 구성
            ExamQuestionDto examQuestionDto = new ExamQuestionDto(question.getId(), savedAnswerNote, question.getNumber(), description.getContent(),
                    descriptionCommentList, question.getAnswer(), question.getChoiceType().name(), question.getScore(),
                    questionChoiceDtoList, examQuestionLearningRecord.getCheckedNumber());
            examQuestionDtoList.add(examQuestionDto);
        }
        return  examQuestionDtoList;
    }

    @Transactional(readOnly = true)
    public List<AnswerNotedTopicQueryDto> getAnswerNotedQuestions(Customer customer) {

        List<ExamQuestionLearningRecord> recordList = examQuestionLearningRecordRepository.queryExamQuestionLearningRecords(customer);
        Map<ExamQuestion, ExamQuestionLearningRecord> answerNoteMap = recordList.stream()
                .collect(Collectors.toMap(ExamQuestionLearningRecord::getExamQuestion, an -> an));
        List<ExamQuestion> questionList = recordList.stream()
                .map(ExamQuestionLearningRecord::getExamQuestion)
                .collect(Collectors.toList());
        Map<Description, List<DescriptionKeyword>> descriptionKeywordMap = descriptionKeywordRepository.queryDescriptionKeywordForExamQuestion(questionList).stream()
                .collect(Collectors.groupingBy(DescriptionKeyword::getDescription));
        Map<Choice, List<ChoiceKeyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForExamQuestion(questionList).stream()
                .collect(Collectors.groupingBy(ChoiceKeyword::getChoice));

        return makeAnswerNotedTopicQueryDtoList(questionList, descriptionKeywordMap, choiceKeywordMap, answerNoteMap);
    }

    public ExamQuestionDto getQuestion(Customer customer, Long examQuestionId) {
        ExamQuestion question = examQuestionRepository.queryExamQuestion(examQuestionId).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });
        List<DescriptionKeyword> descriptionKeywordList = descriptionKeywordRepository.queryDescriptionKeywords(examQuestionId);
        Map<Choice, List<ChoiceKeyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForExamQuestion(examQuestionId).stream()
                .collect(Collectors.groupingBy(ck -> ck.getChoice()));
        ExamQuestionLearningRecord record = examQuestionLearningRecordRepository.findByCustomerAndExamQuestion(customer, question).orElseGet(() -> {
            ExamQuestionLearningRecord newRecord = new ExamQuestionLearningRecord(customer, question);
            examQuestionLearningRecordRepository.save(newRecord);
            return newRecord;
        });

        //보기 - 키워드
        Description description = question.getDescription();
        List<ExamQuestionCommentDto> descriptionCommentList = makeDescriptionCommentList(descriptionKeywordList);

        //선지 - 키워드
        List<Choice> choiceList = question.getChoiceList();
        List<QuestionChoiceDto> questionChoiceDtoList = new ArrayList<>();
        for (Choice choice : choiceList) {
            List<ChoiceKeyword> choiceKeywordList = choiceKeywordMap.get(choice);
            QuestionChoiceDto dto = new QuestionChoiceDto(choice.getContent(), choice.getNumber(), new ArrayList<>());
            if (choiceKeywordList != null) {
                dto = makeQuestionChoiceDto(choice, choiceKeywordList);
            }
            questionChoiceDtoList.add(dto);
        }

        //오답노트
        Boolean savedAnswerNote = record.getAnswerNoted();

        //전체 dto 생성
        return new ExamQuestionDto(question.getId(), savedAnswerNote, question.getNumber(), description.getContent(),
                descriptionCommentList, question.getAnswer(), question.getChoiceType().name(), question.getScore(),
                questionChoiceDtoList, record.getCheckedNumber());
    }

    private QuestionChoiceDto makeQuestionChoiceDto(Choice choice, List<ChoiceKeyword> choiceKeywordList) {
        List<Keyword> keywordList = choiceKeywordList.stream()
                .map(ck -> ck.getKeyword())
                .collect(Collectors.toList());
        List<ExamQuestionCommentDto> commentList = makeCommentList(keywordList);
        return new QuestionChoiceDto(choice.getContent(), choice.getNumber(), commentList);
    }

    private List<ExamQuestionCommentDto> makeDescriptionCommentList(List<DescriptionKeyword> descriptionKeywordList) {
        List<Keyword> keywordList = descriptionKeywordList.stream()
                .map(dk -> dk.getKeyword())
                .collect(Collectors.toList());
        return makeCommentList(keywordList);
    }

    private List<ExamQuestionCommentDto> makeCommentList(List<Keyword> keywordList) {
        return keywordList.stream()
                .map(k -> {
                    Topic topic = k.getTopic();
                    return new ExamQuestionCommentDto(topic.getDateComment(), topic.getChapter().getTitle() + " - " + topic.getTitle(), k.getDateComment(), k.getName(),
                            k.getComment());
                })
                .collect(Collectors.toList());
    }


    @Transactional
    public ExamQuestion saveExamQuestionInfo(Integer roundNumber, ExamQuestionInfoDto examQuestionInfoDto) {
        Round round = checkRound(roundNumber);

        Integer questionNumber = examQuestionInfoDto.getNumber();
        String inputChoiceType = examQuestionInfoDto.getChoiceType();

        //입력받은 choiceType이 옳은 형식인지 확인
        ChoiceType choiceType = checkChoiceType(inputChoiceType);

        //해당 회차에 해당 번호를 가진 문제가 이미 존재하는지 확인
        checkDupQuestionNumber(roundNumber, questionNumber);

        //문제 저장
        ExamQuestion examQuestion = new ExamQuestion(round, examQuestionInfoDto.getNumber(), examQuestionInfoDto.getScore(), examQuestionInfoDto.getAnswer(),choiceType);
        examQuestionRepository.save(examQuestion);

        //보기 생성
        Description description = new Description(examQuestion);
        descriptionRepository.save(description);

        return examQuestion;
    }

    @Transactional
    public Choice saveExamQuestionChoice(Integer roundNumber, Integer questionNumber, ChoiceAddUpdateDto dto) throws IOException {
        checkRound(roundNumber);
        String inputChoiceType = dto.getChoiceType();

        //examQuestion 조회
        ExamQuestion examQuestion = checkExamQuestion(roundNumber, questionNumber);

        //입력받은 choiceType이 옳은 형식인지 확인
        ChoiceType choiceType = checkChoiceType(inputChoiceType);

        //입력 받은 주제 제목들이 DB에 존재하는 주제 제목인지 확인
        Topic answerTopic = checkTopic(dto.getKey());

        //선지 저장
        Choice choice = null;
        if(choiceType.equals(ChoiceType.String)){
            choice = new Choice(choiceType, dto.getChoice(), dto.getComment(), answerTopic, examQuestion);
            choiceRepository.save(choice);
        }
        //선지 저장(이미지)
        else if(choiceType.equals(ChoiceType.Image)){
            String encodedFile = dto.getChoice();
            imageService.checkBase64(encodedFile);
            String choiceUrl = imageService.storeFile(encodedFile);
            choice = new Choice(choiceType, choiceUrl, dto.getComment(), answerTopic, examQuestion);
            choiceRepository.save(choice);
        }
        return choice;
    }



    @Transactional
    public ExamQuestion updateExamQuestionInfo(Integer roundNumber, Integer questionNumber, ExamQuestionInfoDto examQuestionInfoDto) throws IOException {
        checkRound(roundNumber);
        Integer newQuestionNumber = examQuestionInfoDto.getNumber();
        String inputChoiceType = examQuestionInfoDto.getChoiceType();

        //해당 round에 해당 questionNumber를 가진 문제가 존재하는지 확인
        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        //입력받은 choiceType이 옳은 형식인지 확인
        ChoiceType choiceType = checkChoiceType(inputChoiceType);

        //문제번호, 점수 변경
        if (!questionNumber.equals(newQuestionNumber)) {
            checkDupQuestionNumber(roundNumber, newQuestionNumber);
        }
        ExamQuestion updatedExamQuestion = examQuestion.updateExamQuestion(newQuestionNumber, examQuestionInfoDto.getScore(),examQuestionInfoDto.getAnswer() ,choiceType);

        return updatedExamQuestion;
    }

    @Transactional
    public Boolean deleteExamQuestion(Integer roundNumber, Integer questionNumber) {
        checkRound(roundNumber);

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        Description description = examQuestion.getDescription();
        descriptionRepository.delete(description);

        List<Choice> choiceList = examQuestion.getChoiceList();
        choiceRepository.deleteAllInBatch(choiceList);

        examQuestionRepository.delete(examQuestion);
        return true;
    }

    private void checkDupQuestionNumber(Integer roundNumber, Integer questionNumber) {
        examQuestionRepository.queryExamQuestion(roundNumber, questionNumber).ifPresent(eq -> {
            throw new CustomException(DUP_QUESTION_NUMBER);
        });
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    private Round checkRound(Integer roundNumber) {
        return roundRepository.findRoundByNumber(roundNumber).orElseThrow(() -> {
            throw new CustomException(ROUND_NOT_FOUND);
        });
    }

    private ExamQuestion checkExamQuestion(Integer roundNumber, Integer questionNumber) {
        return examQuestionRepository.queryExamQuestion(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });
    }

    private ChoiceType checkChoiceType(String inputChoiceType){
        //입력받은 choiceType이 옳은 형식인지 확인
        Map<String, ChoiceType> map = ChoiceType.getChoiceTypeNameMap();
        ChoiceType choiceType = map.get(inputChoiceType);
        if(choiceType == null){
            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
        }
        return choiceType;
    }


    @Transactional
    public void clearRoundQuestionRecord(Customer customer, Integer roundNumber) {
        Optional<RoundLearningRecord> roundRecordOptional
                = roundLearningRecordRepository.queryRoundLearningRecord(customer, roundNumber);
        if (roundRecordOptional.isPresent()) {
            RoundLearningRecord roundRecord = roundRecordOptional.get();
            List<Long> examQuestionIdList = roundRecord.getRound().getExamQuestionList().stream()
                            .map(e -> e.getId())
                                    .collect(Collectors.toList());
            roundRecord.clearScore();

            List<ExamQuestionLearningRecord> questionRecordList
                    = examQuestionLearningRecordRepository.queryExamQuestionLearningRecords(customer, examQuestionIdList);
            questionRecordList.forEach(q -> q.clear());
        }

    }
}
