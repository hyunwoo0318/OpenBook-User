package Project.OpenBook.Round;

import Project.OpenBook.Round.Domain.Round;
import Project.OpenBook.Round.Repo.RoundRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static Project.OpenBook.Constants.ErrorCode.DUP_ROUND_NUMBER;
import static Project.OpenBook.Constants.ErrorCode.ROUND_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class RoundValidator {
    private final RoundRepository roundRepository;

    public void checkDupRoundNumber(Integer number) {
        roundRepository.findRoundByNumber(number).ifPresent(r -> {
            throw new CustomException(DUP_ROUND_NUMBER);
        });
    }

    public Round checkRound(Integer number) {
        return roundRepository.findRoundByNumber(number).orElseThrow(() -> {
            throw new CustomException(ROUND_NOT_FOUND);
        });
    }
}
