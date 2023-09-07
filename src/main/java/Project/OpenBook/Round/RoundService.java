package Project.OpenBook.Round;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Round.dto.RoundDto;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.DUP_ROUND_NUMBER;
import static Project.OpenBook.Constants.ErrorCode.ROUND_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class RoundService {

    private final RoundRepository roundRepository;


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
        checkDupRoundNumber(number);

        Round round = new Round(date, number);
        roundRepository.save(round);
    }

    @Transactional
    public void updateRound(Integer prevNumber, RoundDto roundDto) {
        Integer newNumber = roundDto.getNumber();
        Integer date = roundDto.getDate();
        Round round = checkRound(prevNumber);

        if (!prevNumber.equals(newNumber)) {
            checkDupRoundNumber(newNumber);
        }


        round.updateRound(date, newNumber);
    }

    public void deleteRound(Integer number) {
        Round round = checkRound(number);

        roundRepository.delete(round);
    }

    private void checkDupRoundNumber(Integer number) {
        roundRepository.findRoundByNumber(number).ifPresent(r -> {
            throw new CustomException(DUP_ROUND_NUMBER);
        });
    }

    private Round checkRound(Integer number) {
        return roundRepository.findRoundByNumber(number).orElseThrow(() -> {
            throw new CustomException(ROUND_NOT_FOUND);
        });
    }
}
