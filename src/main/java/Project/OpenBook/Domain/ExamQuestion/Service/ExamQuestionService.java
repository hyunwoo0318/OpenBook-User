package Project.OpenBook.Domain.ExamQuestion.Service;

import Project.OpenBook.Constants.ChoiceConst;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ExamQuestionDto;
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
    public ExamQuestionDto getExamQuestion(Integer roundNumber, Integer questionNumber) {

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescription(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        Description description = examQuestion.getDescription();
        List<Choice> choiceList = examQuestion.getChoiceList();
        String type = null;

        List<QuestionChoiceDto> choiceDtoList = choiceList.stream()
                .map(c -> new QuestionChoiceDto(c.getContent(), c.getComment(), c.getTopic().getTitle(), c.getId()))
                .collect(Collectors.toList());

        if (!choiceList.isEmpty()) {
            type = choiceList.get(0).getType();
        }

        return new ExamQuestionDto(examQuestion.getNumber(), description.getContent(), description.getComment(), description.getTopic().getTitle(),type,
                choiceDtoList, examQuestion.getScore());
    }

    @Transactional
    public void saveExamQuestion(Integer roundNumber, ExamQuestionDto examQuestionDto) throws IOException {
        Round round = checkRound(roundNumber);

        String answer = examQuestionDto.getAnswer();
        Integer questionNumber = examQuestionDto.getNumber();
        String choiceType = examQuestionDto.getChoiceType();

        //해당 회차에 해당 번호를 가진 문제가 존재하는 경우
        checkDupQuestionNumber(roundNumber, questionNumber);
        
        List<QuestionChoiceDto> choiceDtoList = examQuestionDto.getChoiceList();
        List<String> topicTitleList = choiceDtoList.stream()
                .map(QuestionChoiceDto::getKey)
                .collect(Collectors.toList());
        topicTitleList.add(answer);

        //입력 받은 주제 제목들이 DB에 존재하는 주제 제목인지 확인
        Map<String, Topic> topicMap = topicRepository.queryTopicsByTopicTitleList(topicTitleList).stream()
                .collect(Collectors.toMap(
                        Topic::getTitle,
                        t -> t));
        
        for (String topicTitle : topicTitleList) {
            if (topicMap.get(topicTitle) == null) {
                throw new CustomException(TOPIC_NOT_FOUND);
            }
        }

        //문제 저장
        ExamQuestion examQuestion = new ExamQuestion(round, examQuestionDto.getNumber(), examQuestionDto.getScore());
        examQuestionRepository.save(examQuestion);

        //보기 저장
        Description description = new Description(examQuestionDto.getDescription(), examQuestionDto.getDescriptionComment(),
                topicMap.get(examQuestionDto.getAnswer()), examQuestion);
        descriptionRepository.save(description);

        //선지 전체 저장(일반 선지)
        List<Choice> choiceList = new ArrayList<>();
        if(choiceType.equals(ChoiceConst.CHOICE_STRING)){
            choiceList = choiceDtoList.stream()
                    .map(choiceDto -> new Choice(choiceType,choiceDto.getChoice(), choiceDto.getComment(),
                            topicMap.get(choiceDto.getKey()), examQuestion))
                    .collect(Collectors.toList());
        }
        //선지 전체 저장(이미지)
        else if(choiceType.equals(ChoiceConst.CHOICE_IMAGE)){
            for (QuestionChoiceDto choiceDto : choiceDtoList) {
                String encodedFile = choiceDto.getChoice();
                imageService.checkBase64(encodedFile);
                imageService.storeFile(encodedFile);
                choiceList.add(new Choice(choiceType,choiceDto.getChoice(), choiceDto.getComment(),
                        topicMap.get(choiceDto.getKey()), examQuestion));
            }
        }else{
            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
        }
        choiceRepository.saveAll(choiceList);

    }



    @Transactional
    public void updateExamQuestion(Integer roundNumber, Integer questionNumber, ExamQuestionDto examQuestionDto) throws IOException {
        checkRound(roundNumber);
        Integer newQuestionNumber = examQuestionDto.getNumber();
        String newChoiceType = examQuestionDto.getChoiceType();

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescription(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        //문제번호, 점수 변경
        if (!questionNumber.equals(newQuestionNumber)) {
            checkDupQuestionNumber(roundNumber, newQuestionNumber);
        }
        examQuestion.updateExamQuestion(newQuestionNumber, examQuestionDto.getScore());

        //보기 변경
        Description description = examQuestion.getDescription();
        description.updateContent(examQuestionDto.getDescription(), examQuestionDto.getDescriptionComment());

        //선지 변경
        List<QuestionChoiceDto> choiceDtoList = examQuestionDto.getChoiceList();
        List<Long> choiceIdList = choiceDtoList.stream()
                .filter(dto -> dto.getId() != null)
                .map(QuestionChoiceDto::getId)
                .collect(Collectors.toList());
        Map<Long, Choice> choiceMap = choiceRepository.queryChoicesById(choiceIdList).stream()
                .collect(Collectors.toMap(Choice::getId, choice -> choice));

        //선지 변경 -> 일반 선지
        if (newChoiceType.equals(ChoiceConst.CHOICE_STRING)) {
            for (QuestionChoiceDto dto : choiceDtoList) {
                Long choiceId = dto.getId();
                Topic topic = checkTopic(dto.getKey());
                if (choiceId != null) {
                    //기존에 존재하는 선지
                    Choice choice = choiceMap.get(choiceId);

                    choice.updateChoice(dto.getChoice(), dto.getComment(), topic);
                }else{
                    //새로 추가된 선지
                    Choice choice = new Choice(newChoiceType, dto.getChoice(), dto.getComment(), topic, examQuestion);
                    choiceRepository.save(choice);
                }
            }
        }
        //선지 변경 -> 이미지
        else if (newChoiceType.equals(ChoiceConst.CHOICE_IMAGE)) {
            for (QuestionChoiceDto dto : choiceDtoList) {
                Long choiceId = dto.getId();
                Topic topic = checkTopic(dto.getKey());
                if (choiceId != null) {
                    //기존에 존재하는 선지
                    Choice choice = choiceMap.get(choiceId);

                    choice.updateChoice(dto.getChoice(), dto.getComment(), topic);
                }else{
                    //새로 추가된 선지
                    String encodedFile = dto.getChoice();
                    imageService.checkBase64(encodedFile);
                    imageService.storeFile(encodedFile);
                    Choice choice = new Choice(newChoiceType, dto.getChoice(), dto.getComment(), topic, examQuestion);
                    choiceRepository.save(choice);
                }
            }
        }else{
            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
        }
    }

    @Transactional
    public void deleteExamQuestion(Integer roundNumber, Integer questionNumber) {
        Round round = checkRound(roundNumber);

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescription(roundNumber, questionNumber).orElseThrow(() -> {
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
}
