package Project.OpenBook.Domain.Choice.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChoiceService {


//    @Transactional(readOnly = true)
//    public List<ChoiceCommentQueryDto> queryQuestionChoices(Integer roundNumber, Integer questionNumber) {
//
//        List<ChoiceCommentQueryDto> retList = new ArrayList<>();
//        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestion(roundNumber, questionNumber).orElseThrow(() -> {
//            throw new CustomException(QUESTION_NOT_FOUND);
//        });
//
//        List<Choice> choiceList = examQuestion.getChoiceList();
//        Map<Choice, List<ChoiceCommentInfoDto>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForAdmin(choiceList);
//
//        for (Choice choice : choiceList) {
//            List<ChoiceCommentInfoDto> commentList = new ArrayList<>();
//            List<ChoiceCommentInfoDto> dtoKeywordList = choiceKeywordMap.get(choice);
//            if (dtoKeywordList != null) {
//                commentList.addAll(dtoKeywordList);
//            }
//
//            retList.add(new ChoiceCommentQueryDto(choice.getContent(), choice.getNumber(), choice.getId(),
//                    choice.getType().name(), commentList));
//        }
//
//
//        return retList;
//    }


}
