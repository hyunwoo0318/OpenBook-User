package Project.OpenBook.Domain.Round;

import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.General.ExamQuestionRepository;
import Project.OpenBook.Domain.Round.Domain.Round;
import Project.OpenBook.Domain.Round.dto.RoundDto;
import Project.OpenBook.Domain.Round.dto.RoundInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final RoundValidator roundValidator;


    public List<RoundDto> queryRounds() {
        return roundRepository.findAll().stream()
                .sorted(Comparator.comparing(Round::getNumber))
                .map(r -> new RoundDto(r.getNumber(), r.getDate()))
                .collect(Collectors.toList());
    }

    public void createRound(RoundDto roundDto) {
        Integer number = roundDto.getNumber();
        Integer date = roundDto.getDate();

        //회차 번호 중복 확인
        roundValidator.checkDupRoundNumber(number);

        Round round = new Round(date, number);
        roundRepository.save(round);
    }

    @Transactional
    public void updateRound(Integer prevNumber, RoundDto roundDto) {
        Integer newNumber = roundDto.getNumber();
        Integer date = roundDto.getDate();
        Round round = roundValidator.checkRound(prevNumber);

        if (!prevNumber.equals(newNumber)) {
            roundValidator.checkDupRoundNumber(newNumber);
        }


        round.updateRound(date, newNumber);
    }

    public void deleteRound(Integer number) {
        Round round = roundValidator.checkRound(number);

        roundRepository.delete(round);
    }

    public RoundInfoDto queryRound(Integer number) {
        Round round = roundValidator.checkRound(number);
        return new RoundInfoDto(round.getDate());
    }

    public List<Integer> queryRoundQuestions(Integer number) {
        List<ExamQuestion> examQuestionList = examQuestionRepository.queryExamQuestions(number);
        return examQuestionList.stream()
                .map(ExamQuestion::getNumber)
                .collect(Collectors.toList());
    }
}
