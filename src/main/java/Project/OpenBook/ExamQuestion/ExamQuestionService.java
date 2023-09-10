package Project.OpenBook.ExamQuestion;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Domain.Description;
import Project.OpenBook.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Topic.Domain.Topic;
import Project.OpenBook.Dto.question.QuestionChoiceDto;
import Project.OpenBook.ExamQuestion.Repo.General.ExamQuestionRepository;
import Project.OpenBook.ExamQuestion.Controller.dto.ExamQuestionDto;
import Project.OpenBook.Repository.choice.ChoiceRepository;
import Project.OpenBook.Repository.description.DescriptionRepository;
import Project.OpenBook.Topic.Repo.TopicRepository;
import Project.OpenBook.Round.Domain.Round;
import Project.OpenBook.Round.Repo.RoundRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    public ExamQuestionDto getExamQuestion(Integer roundNumber, Integer questionNumber) {

        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescription(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        Description description = examQuestion.getDescription();
        List<Choice> choiceList = examQuestion.getChoiceList();

        List<QuestionChoiceDto> choiceDtoList = choiceList.stream()
                .map(c -> new QuestionChoiceDto(c.getContent(), c.getComment(), c.getTopic().getTitle(), c.getId()))
                .collect(Collectors.toList());

        return new ExamQuestionDto(examQuestion.getNumber(), description.getContent(), description.getComment(), description.getTopic().getTitle(),
                choiceDtoList, examQuestion.getScore());
    }

    @Transactional
    public void saveExamQuestion(Integer roundNumber, ExamQuestionDto examQuestionDto) {
        Round round = checkRound(roundNumber);

        String answer = examQuestionDto.getAnswer();
        Integer questionNumber = examQuestionDto.getNumber();
        
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
        
        //TODO : 해당 주제들이 DB에 존재하는지 확인

        //문제 저장
        ExamQuestion examQuestion = new ExamQuestion(round, examQuestionDto.getNumber(), examQuestionDto.getScore());
        examQuestionRepository.save(examQuestion);

        //보기 저장
        Description description = new Description(examQuestionDto.getDescription(), examQuestionDto.getDescriptionComment(),
                topicMap.get(examQuestionDto.getAnswer()), examQuestion);
        descriptionRepository.save(description);

        //선지 전체 저장
        List<Choice> choiceList = choiceDtoList.stream()
                .map(choiceDto -> new Choice(choiceDto.getChoice(), choiceDto.getComment(),
                        topicMap.get(choiceDto.getKey()), examQuestion))
                .collect(Collectors.toList());
        choiceRepository.saveAll(choiceList);
    }

    private Round checkRound(Integer roundNumber) {
        return roundRepository.findRoundByNumber(roundNumber).orElseThrow(() -> {
            throw new CustomException(ROUND_NOT_FOUND);
        });
    }

    @Transactional
    public void updateExamQuestion(Integer roundNumber, Integer questionNumber, ExamQuestionDto examQuestionDto) {
        checkRound(roundNumber);
        Integer newQuestionNumber = examQuestionDto.getNumber();

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

        for (QuestionChoiceDto dto : choiceDtoList) {
            Long choiceId = dto.getId();
            Topic topic = checkTopic(dto.getKey());
            if (choiceId != null) {
                //기존에 존재하는 선지
                Choice choice = choiceMap.get(choiceId);

                choice.updateChoice(dto.getChoice(), dto.getComment(), topic);
            }else{
                //새로 추가된 선지
                Choice choice = new Choice(dto.getChoice(), dto.getComment(), topic, examQuestion);
                choiceRepository.save(choice);
            }
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
}
