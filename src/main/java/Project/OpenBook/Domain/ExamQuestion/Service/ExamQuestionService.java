package Project.OpenBook.Domain.ExamQuestion.Service;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ChoiceAddUpdateDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionChoiceDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionDto;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionInfoDto;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Image.ImageService;
import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.Description.Repository.DescriptionRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

    private final ImageService imageService;

    @Transactional(readOnly = true)
    public ExamQuestionInfoDto getExamQuestionInfo(Integer roundNumber, Integer questionNumber) {

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        Description description = examQuestion.getDescription();

        ChoiceType choiceType = examQuestion.getChoiceType();

        return new ExamQuestionInfoDto(examQuestion.getNumber(), description.getContent(), description.getComment(),
                description.getTopic().getTitle(),choiceType.name(),examQuestion.getScore());
    }

    @Transactional(readOnly = true)
    public List<ExamQuestionDto> getRoundQuestions(Integer roundNumber) {
        List<ExamQuestionDto> examQuestionDtoList = new ArrayList<>();
        Round round = checkRound(roundNumber);

        List<ExamQuestion> examQuestionList = examQuestionRepository.queryExamQuestionsWithDescriptionAndTopic(roundNumber);
        for (ExamQuestion examQuestion : examQuestionList) {
            String topicTitle = examQuestion.getDescription().getTopic().getTitle();
            List<QuestionChoiceDto> choiceDtoList = examQuestion.getChoiceList().stream()
                    .map(ch -> new QuestionChoiceDto(ch.getContent(), ch.getComment(), ch.getTopic().getTitle(), ch.getId()))
                    .collect(Collectors.toList());
            ExamQuestionDto dto = new ExamQuestionDto(examQuestion.getNumber(), examQuestion.getDescription().getContent(), examQuestion.getDescription().getComment(),
                    topicTitle, examQuestion.getChoiceType().name(), examQuestion.getScore(), choiceDtoList);

            examQuestionDtoList.add(dto);
        }

        return examQuestionDtoList;

    }

    @Transactional(readOnly = true)
    public ExamQuestionChoiceDto getExamQuestionChoices(Integer roundNumber, Integer questionNumber) {
        ExamQuestion examQuestion = checkExamQuestion(roundNumber, questionNumber);
        List<QuestionChoiceDto> choiceDtoList = examQuestion.getChoiceList().stream()
                .map(ch -> new QuestionChoiceDto(ch.getContent(), ch.getComment(), ch.getTopic().getTitle(), ch.getId()))
                .collect(Collectors.toList());
        return new ExamQuestionChoiceDto(choiceDtoList);
    }

    @Transactional
    public void saveExamQuestionInfo(Integer roundNumber, ExamQuestionInfoDto examQuestionInfoDto) throws IOException {
        Round round = checkRound(roundNumber);

        String answer = examQuestionInfoDto.getAnswer();
        Integer questionNumber = examQuestionInfoDto.getNumber();
        String inputChoiceType = examQuestionInfoDto.getChoiceType();

        //입력받은 choiceType이 옳은 형식인지 확인
        ChoiceType choiceType = checkChoiceType(inputChoiceType);

        //해당 회차에 해당 번호를 가진 문제가 이미 존재하는지 확인
        checkDupQuestionNumber(roundNumber, questionNumber);


        //입력 받은 주제 제목들이 DB에 존재하는 주제 제목인지 확인
        Topic answerTopic = checkTopic(answer);

        //문제 저장
        ExamQuestion examQuestion = new ExamQuestion(round, examQuestionInfoDto.getNumber(), examQuestionInfoDto.getScore(), choiceType);
        examQuestionRepository.save(examQuestion);

        //보기 저장
        String descriptionEncodedFile = examQuestionInfoDto.getDescription();
        imageService.checkBase64(descriptionEncodedFile);
        String descriptionUrl = imageService.storeFile(descriptionEncodedFile);
        Description description = new Description(descriptionUrl, examQuestionInfoDto.getDescriptionComment(),
                answerTopic, examQuestion);
        descriptionRepository.save(description);


    }

    @Transactional
    public void saveExamQuestionChoice(Integer roundNumber, Integer questionNumber, ChoiceAddUpdateDto dto) throws IOException {
        Round round = checkRound(roundNumber);
        String inputChoiceType = dto.getChoiceType();

        //examQuestion 조회
        ExamQuestion examQuestion = checkExamQuestion(roundNumber, questionNumber);

        //입력 받은 주제 제목들이 DB에 존재하는 주제 제목인지 확인
        Topic answerTopic = checkTopic(dto.getKey());

        //입력받은 choiceType이 옳은 형식인지 확인
        ChoiceType choiceType = checkChoiceType(inputChoiceType);

        //선지 저장
        if(choiceType.equals(ChoiceType.String)){
            Choice choice = new Choice(choiceType, dto.getChoice(), dto.getComment(), answerTopic, examQuestion);
            choiceRepository.save(choice);
        }
        //선지 전체 저장(이미지)
        else if(choiceType.equals(ChoiceType.Image)){
            String encodedFile = dto.getChoice();
            imageService.checkBase64(encodedFile);
            String choiceUrl = imageService.storeFile(encodedFile);
            Choice choice = new Choice(choiceType, choiceUrl, dto.getComment(), answerTopic, examQuestion);
            choiceRepository.save(choice);
        }else{
            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
        }
    }



    @Transactional
    public void updateExamQuestion(Integer roundNumber, Integer questionNumber, ExamQuestionInfoDto examQuestionInfoDto) throws IOException {
        checkRound(roundNumber);
        Integer newQuestionNumber = examQuestionInfoDto.getNumber();
        String inputChoiceType = examQuestionInfoDto.getChoiceType();

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        //입력받은 choiceType이 옳은 형식인지 확인
        ChoiceType choiceType = checkChoiceType(inputChoiceType);

        //문제번호, 점수 변경
        if (!questionNumber.equals(newQuestionNumber)) {
            checkDupQuestionNumber(roundNumber, newQuestionNumber);
        }
        examQuestion.updateExamQuestion(newQuestionNumber, examQuestionInfoDto.getScore(), choiceType);

        //보기 변경
        Description description = examQuestion.getDescription();
        String descriptionUrl = examQuestionInfoDto.getDescription();
        if (!descriptionUrl.startsWith("https")) {
            imageService.checkBase64(descriptionUrl);
            descriptionUrl = imageService.storeFile(descriptionUrl);
        }
        description.updateContent(descriptionUrl, examQuestionInfoDto.getDescriptionComment());
    }

    @Transactional
    public void deleteExamQuestion(Integer roundNumber, Integer questionNumber) {
        Round round = checkRound(roundNumber);

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescriptionAndTopic(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        Description description = examQuestion.getDescription();
        descriptionRepository.delete(description);

        List<Choice> choiceList = examQuestion.getChoiceList();
        choiceRepository.deleteAllInBatch(choiceList);

        examQuestionRepository.delete(examQuestion);
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
