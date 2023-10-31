package Project.OpenBook.Domain.ExamQuestion.Service;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.AnswerNote.Domain.AnswerNote;
import Project.OpenBook.Domain.AnswerNote.Repository.AnswerNoteRepository;
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
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ChoiceAddUpdateDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionCommentDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionInfoDto;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ExamQuestionService {
    private final ExamQuestionRepository examQuestionRepository;
    private final RoundRepository roundRepository;
    private final TopicRepository topicRepository;
    private final DescriptionRepository descriptionRepository;
    private final ChoiceRepository choiceRepository;
    private final ChoiceKeywordRepository choiceKeywordRepository;
    private final DescriptionKeywordRepository descriptionKeywordRepository;
    private final AnswerNoteRepository answerNoteRepository;
    private final ImageService imageService;

    @Transactional(readOnly = true)
    public ExamQuestionInfoDto getExamQuestionInfo(Integer roundNumber, Integer questionNumber) {

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestion(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        ChoiceType choiceType = examQuestion.getChoiceType();

        return new ExamQuestionInfoDto(examQuestion.getNumber(), examQuestion.getAnswer(), choiceType.name(), examQuestion.getScore());
    }

    @Transactional(readOnly = true)
    public List<ExamQuestionDto> getRoundQuestions(Customer customer, Integer roundNumber) {
        List<ExamQuestionDto> examQuestionDtoList = new ArrayList<>();
        checkRound(roundNumber);

        List<ExamQuestion> examQuestionList = examQuestionRepository.queryExamQuestionsForExamQuestionList(roundNumber);
        Map<Description, List<DescriptionKeyword>> descriptionKeywordMap = descriptionKeywordRepository.queryDescriptionKeywordForExamQuestion(roundNumber).stream()
                .collect(Collectors.groupingBy(dk -> dk.getDescription()));
        Map<Choice, List<ChoiceKeyword>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForExamQuestion(roundNumber).stream()
                .collect(Collectors.groupingBy(ck -> ck.getChoice()));
        Map<ExamQuestion, AnswerNote> answerNoteMap = answerNoteRepository.queryAnswerNotes(roundNumber, customer).stream()
                .collect(Collectors.toMap(AnswerNote::getExamQuestion, an -> an));

        for (ExamQuestion question : examQuestionList) {
            //보기 -> 보기 키워드 구성
            Description description = question.getDescription();
            List<DescriptionKeyword> descriptionKeywordList = descriptionKeywordMap.get(description);
            List<ExamQuestionCommentDto> descriptionCommentList = new ArrayList<>();
            if (descriptionKeywordList != null) {
                List<Keyword> keywordForDescriptionList = descriptionKeywordList.stream()
                        .map(dk -> dk.getKeyword())
                        .collect(Collectors.toList());
                descriptionCommentList = makeDescriptionCommentList(keywordForDescriptionList);
            }


            //선지 -> 선지 키워드 구성
            List<Choice> choiceList = question.getChoiceList();
            List<QuestionChoiceDto> questionChoiceDtoList = new ArrayList<>();
            for (Choice choice : choiceList) {
                List<ChoiceKeyword> choiceKeywordList = choiceKeywordMap.get(choice);
                QuestionChoiceDto dto;

                if (choiceKeywordList != null) {
                    List<Keyword> keywordForChoiceList = choiceKeywordList.stream()
                            .map(ck -> ck.getKeyword())
                            .collect(Collectors.toList());
                    dto = makeQuestionChoiceDto(choice, keywordForChoiceList);
                }else{
                    dto = new QuestionChoiceDto(choice.getContent(), choice.getNumber(), new ArrayList<>());
                }
                questionChoiceDtoList.add(dto);
            }

            //오답노트 구성
            AnswerNote answerNote = answerNoteMap.get(question);
            Boolean savedAnswerNote = false;
            if (answerNote != null) {
                savedAnswerNote = true;
            }

            ExamQuestionDto examQuestionDto = new ExamQuestionDto(question.getId(), savedAnswerNote, question.getNumber(), description.getContent(),
                    descriptionCommentList, question.getAnswer(), question.getChoiceType().name(), question.getScore(),
                    questionChoiceDtoList);
            examQuestionDtoList.add(examQuestionDto);

        }


        return examQuestionDtoList;

    }

    private QuestionChoiceDto makeQuestionChoiceDto(Choice choice, List<Keyword> keywordList) {
        List<ExamQuestionCommentDto> commentList = makeDescriptionCommentList(keywordList);
        return new QuestionChoiceDto(choice.getContent(), choice.getNumber(), commentList);
    }

    private List<ExamQuestionCommentDto> makeDescriptionCommentList(List<Keyword> keywordList) {
        return keywordList.stream()
                .map(k -> {
                    Topic topic = k.getTopic();
                    return new ExamQuestionCommentDto(topic.getDateComment(), topic.getTitle(), k.getDateComment(), k.getName(),
                            k.getComment());
                })
                .collect(Collectors.toList());
    }

//    @Transactional(readOnly = true)
//    public ExamQuestionChoiceDto getExamQuestionChoices(Integer roundNumber, Integer questionNumber) {
//        ExamQuestion examQuestion = checkExamQuestion(roundNumber, questionNumber);
//        List<QuestionChoiceDto> choiceDtoList = examQuestion.getChoiceList().stream()
//                .map(ch -> new QuestionChoiceDto(ch.getContent(), ch.getComment(), ch.getTopic().getTitle(), ch.getId()))
//                .collect(Collectors.toList());
//        return new ExamQuestionChoiceDto(choiceDtoList);
//    }

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

//        //보기 저장
//        String descriptionEncodedFile = examQuestionInfoDto.getDescription();
//        imageService.checkBase64(descriptionEncodedFile);
//        String descriptionUrl = imageService.storeFile(descriptionEncodedFile);
//        Description description = new Description(descriptionUrl, examQuestionInfoDto.getDescriptionComment(),
//                answerTopic, examQuestion);
//        descriptionRepository.save(description);

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

//        //보기 변경
//        Description description = examQuestion.getDescription();
//        String descriptionUrl = examQuestionInfoDto.getDescription();
//        if (!descriptionUrl.startsWith("https")) {
//            imageService.checkBase64(descriptionUrl);
//            descriptionUrl = imageService.storeFile(descriptionUrl);
//        }
//        description.updateContent(descriptionUrl, examQuestionInfoDto.getDescriptionComment());

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



}
