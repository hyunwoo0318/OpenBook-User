package Project.OpenBook.Domain.Era;

import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.DUP_ERA_NAME;
import static Project.OpenBook.Constants.ErrorCode.ERA_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class EraService {

    private final EraRepository eraRepository;


    @Transactional(readOnly = true)
    public List<EraDto> queryEras() {
        return eraRepository.findAll().stream()
                .map(e -> new EraDto(e.getName()))
                .collect(Collectors.toList());
    }

    @Transactional
    public void createEra(EraDto eraDto) {
        String name = eraDto.getName();
        checkDupEraName(name);

        Era era = new Era(name);
        eraRepository.save(era);
    }



    private void checkDupEraName(String name) {
        eraRepository.findByName(name).ifPresent(e -> {
            throw new CustomException(DUP_ERA_NAME);
        });
    }

    private Era checkEra(String name) {
        return eraRepository.findByName(name).orElseThrow(() -> {
            throw new CustomException(ERA_NOT_FOUND);
        });
    }

    @Transactional
    public void updateEra(String prevEraName, EraDto eraDto) {
        Era era = checkEra(prevEraName);
        String newEraName = eraDto.getName();
        if (!prevEraName.equals(newEraName)) {
            checkDupEraName(newEraName);
        }

        era.changeName(newEraName);
    }

    @Transactional
    public void deleteEra(String eraName) {
        Era era = checkEra(eraName);

        List<QuestionCategory> questionCategoryList = era.getQuestionCategoryList();
        if (!questionCategoryList.isEmpty()) {
            throw new CustomException(ERA_NOT_FOUND);
        }

        eraRepository.delete(era);
    }
}
