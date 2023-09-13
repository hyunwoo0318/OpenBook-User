package Project.OpenBook.Domain.Round.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.RoundValidator;
import Project.OpenBook.Domain.Round.dto.RoundDto;
import Project.OpenBook.Domain.Round.dto.RoundInfoDto;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.ROUND_HAS_QUESTION;

@Service
@RequiredArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final RoundValidator roundValidator;


    @Transactional(readOnly = true)
    public List<RoundDto> queryRounds() {
        return roundRepository.findAll().stream()
                .sorted(Comparator.comparing(Round::getNumber))
                .map(r -> new RoundDto(r.getNumber(), r.getDate()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Round createRound(RoundDto roundDto) {
        Integer number = roundDto.getNumber();
        Integer date = roundDto.getDate();

        //회차 번호 중복 확인
        roundValidator.checkDupRoundNumber(number);

        Round round = new Round(date, number);
        roundRepository.save(round);

        return round;
    }

    @Transactional
    public Round updateRound(Integer prevNumber, RoundDto roundDto) {
        Integer newNumber = roundDto.getNumber();
        Integer date = roundDto.getDate();
        Round round = roundValidator.checkRound(prevNumber);

        if (!prevNumber.equals(newNumber)) {
            roundValidator.checkDupRoundNumber(newNumber);
        }

        round.updateRound(date, newNumber);
        return round;
    }

    @Transactional
    public Boolean deleteRound(Integer number) {
        Round round = roundValidator.checkRound(number);

        List<ExamQuestion> examQuestionList = round.getExamQuestionList();
        if (!examQuestionList.isEmpty()) {
            throw new CustomException(ROUND_HAS_QUESTION);
        }

        roundRepository.delete(round);
        return true;
    }

    @Transactional(readOnly = true)
    public RoundInfoDto queryRound(Integer number) {
        Round round = roundValidator.checkRound(number);
        return new RoundInfoDto(round.getDate());
    }

    @Transactional(readOnly = true)
    public List<Integer> queryRoundQuestions(Integer number) {
        Round round = roundValidator.checkRound(number);
        return round.getExamQuestionList().stream()
                .map(ExamQuestion::getNumber)
                .collect(Collectors.toList());
    }
}
