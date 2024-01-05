package Project.OpenBook.Domain.ExamQuestion.Service;

import static Project.OpenBook.Constants.ErrorCode.QUESTION_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.ROUND_NOT_FOUND;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.QuestionComment.ChoiceKeyword.ChoiceKeyword;
import Project.OpenBook.Domain.QuestionComment.ChoiceKeyword.ChoiceKeywordRepository;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.QuestionComment.DescriptionKeyword.DescriptionKeyword;
import Project.OpenBook.Domain.QuestionComment.DescriptionKeyword.DescriptionKeywordRepository;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionCommentDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionInfoDto;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExamQuestionService {

    private final ExamQuestionRepository examQuestionRepository;
    private final RoundRepository roundRepository;
    private final RoundLearningRecordRepository roundLearningRecordRepository;
    private final TopicRepository topicRepository;
    private final ChoiceKeywordRepository choiceKeywordRepository;
    private final DescriptionKeywordRepository descriptionKeywordRepository;
    private final ExamQuestionLearningRecordRepository examQuestionLearningRecordRepository;

    @Transactional(readOnly = true)
    public ExamQuestionInfoDto getExamQuestionInfo(Integer roundNumber, Integer questionNumber) {

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestion(roundNumber,
            questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        ChoiceType choiceType = examQuestion.getChoiceType();

        return new ExamQuestionInfoDto(examQuestion.getNumber(), examQuestion.getAnswer(),
            choiceType.name(), examQuestion.getScore());
    }

    @Transactional(readOnly = true)
    public List<ExamQuestionDto> getRoundQuestions(Customer customer, Integer roundNumber)
        throws JsonProcessingException {
        checkRound(roundNumber);

//        try {
//            List<ExamQuestionDto> questionListFromRedis = redisService.getQuestionListFromRedis(roundNumber);
//        } catch (JsonProcessingException e) {
//            //해당 값 삭제 로직
//        }

        List<ExamQuestion> examQuestionList = examQuestionRepository.queryExamQuestionsForExamQuestionList(
            roundNumber);
        Map<Description, List<DescriptionKeyword>> descriptionKeywordMap = descriptionKeywordRepository.queryDescriptionKeywordForExamQuestion(
                roundNumber).stream()
            .collect(Collectors.groupingBy(DescriptionKeyword::getDescription));
        Map<Choice, List<ChoiceKeyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForExamQuestion(
                roundNumber).stream()
            .collect(Collectors.groupingBy(ChoiceKeyword::getChoice));
        Map<ExamQuestion, ExamQuestionLearningRecord> answerNoteMap = examQuestionLearningRecordRepository.queryExamQuestionLearningRecords(
                roundNumber, customer).stream()
            .collect(Collectors.toMap(ExamQuestionLearningRecord::getExamQuestion, an -> an));

        return getExamQuestionDtoList(examQuestionList, descriptionKeywordMap, choiceKeywordMap,
            answerNoteMap);
    }


    private List<ExamQuestionDto> getExamQuestionDtoList(List<ExamQuestion> examQuestionList,
        Map<Description, List<DescriptionKeyword>> descriptionKeywordMap,
        Map<Choice, List<ChoiceKeyword>> choiceKeywordMap,
        Map<ExamQuestion, ExamQuestionLearningRecord> answerNoteMap) {
        List<ExamQuestionDto> examQuestionDtoList = new ArrayList<>();

        for (ExamQuestion question : examQuestionList) {
            //보기 -> 보기 키워드 구성
            Description description = question.getDescription();
            List<DescriptionKeyword> descriptionKeywordList = descriptionKeywordMap.get(
                description);
            List<ExamQuestionCommentDto> descriptionCommentList = new ArrayList<>();
            if (descriptionKeywordList != null) {
                descriptionCommentList = makeDescriptionCommentList(descriptionKeywordList);
            }

            //선지 -> 선지 키워드 구성
            List<Choice> choiceList = question.getChoiceList();
            List<QuestionChoiceDto> questionChoiceDtoList = new ArrayList<>();
            for (Choice choice : choiceList) {
                List<ChoiceKeyword> choiceKeywordList = choiceKeywordMap.get(choice);
                QuestionChoiceDto dto = new QuestionChoiceDto(choice.getContent(),
                    choice.getNumber(), new ArrayList<>());
                if (choiceKeywordList != null) {
                    dto = makeQuestionChoiceDto(choice, choiceKeywordList);
                }
                questionChoiceDtoList.add(dto);
            }

            //오답노트 구성
            ExamQuestionLearningRecord examQuestionLearningRecord = answerNoteMap.get(question);
            Boolean savedAnswerNote = examQuestionLearningRecord.getAnswerNoted();

            //전체 ExamQuestionDto 구성
            ExamQuestionDto examQuestionDto = new ExamQuestionDto(question.getId(), savedAnswerNote,
                question.getNumber(), description.getContent(),
                descriptionCommentList, question.getAnswer(), question.getChoiceType().name(),
                question.getScore(),
                questionChoiceDtoList, examQuestionLearningRecord.getCheckedNumber());
            examQuestionDtoList.add(examQuestionDto);
        }
        return examQuestionDtoList;
    }


    public ExamQuestionDto getQuestion(Customer customer, Long examQuestionId) {
        ExamQuestion question = examQuestionRepository.queryExamQuestion(examQuestionId)
            .orElseThrow(() -> {
                throw new CustomException(QUESTION_NOT_FOUND);
            });
        List<DescriptionKeyword> descriptionKeywordList = descriptionKeywordRepository.queryDescriptionKeywords(
            examQuestionId);
        Map<Choice, List<ChoiceKeyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForExamQuestion(
                examQuestionId).stream()
            .collect(Collectors.groupingBy(ck -> ck.getChoice()));
        ExamQuestionLearningRecord record = examQuestionLearningRecordRepository.findByCustomerAndExamQuestion(
            customer, question).orElseGet(() -> {
            ExamQuestionLearningRecord newRecord = new ExamQuestionLearningRecord(customer,
                question);
            examQuestionLearningRecordRepository.save(newRecord);
            return newRecord;
        });

        //보기 - 키워드
        Description description = question.getDescription();
        List<ExamQuestionCommentDto> descriptionCommentList = makeDescriptionCommentList(
            descriptionKeywordList);

        //선지 - 키워드
        List<Choice> choiceList = question.getChoiceList();
        List<QuestionChoiceDto> questionChoiceDtoList = new ArrayList<>();
        for (Choice choice : choiceList) {
            List<ChoiceKeyword> choiceKeywordList = choiceKeywordMap.get(choice);
            QuestionChoiceDto dto = new QuestionChoiceDto(choice.getContent(), choice.getNumber(),
                new ArrayList<>());
            if (choiceKeywordList != null) {
                dto = makeQuestionChoiceDto(choice, choiceKeywordList);
            }
            questionChoiceDtoList.add(dto);
        }

        //오답노트
        Boolean savedAnswerNote = record.getAnswerNoted();

        //전체 dto 생성
        return new ExamQuestionDto(question.getId(), savedAnswerNote, question.getNumber(),
            description.getContent(),
            descriptionCommentList, question.getAnswer(), question.getChoiceType().name(),
            question.getScore(),
            questionChoiceDtoList, record.getCheckedNumber());
    }

    private QuestionChoiceDto makeQuestionChoiceDto(Choice choice,
        List<ChoiceKeyword> choiceKeywordList) {
        List<Keyword> keywordList = choiceKeywordList.stream()
            .map(ck -> ck.getKeyword())
            .collect(Collectors.toList());
        List<ExamQuestionCommentDto> commentList = makeCommentList(keywordList);
        return new QuestionChoiceDto(choice.getContent(), choice.getNumber(), commentList);
    }

    private List<ExamQuestionCommentDto> makeDescriptionCommentList(
        List<DescriptionKeyword> descriptionKeywordList) {
        List<Keyword> keywordList = descriptionKeywordList.stream()
            .map(dk -> dk.getKeyword())
            .collect(Collectors.toList());
        return makeCommentList(keywordList);
    }

    private List<ExamQuestionCommentDto> makeCommentList(List<Keyword> keywordList) {
        return keywordList.stream()
            .map(k -> {
                Topic topic = k.getTopic();
                return new ExamQuestionCommentDto(topic.getDateComment(),
                    topic.getChapter().getTitle() + " - " + topic.getTitle(), k.getDateComment(),
                    k.getName(),
                    k.getComment());
            })
            .collect(Collectors.toList());
    }


    private Round checkRound(Integer roundNumber) {
        return roundRepository.findRoundByNumber(roundNumber).orElseThrow(() -> {
            throw new CustomException(ROUND_NOT_FOUND);
        });
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
                = examQuestionLearningRecordRepository.queryExamQuestionLearningRecords(customer,
                examQuestionIdList);
            questionRecordList.forEach(q -> q.clear());
        }

    }
}
