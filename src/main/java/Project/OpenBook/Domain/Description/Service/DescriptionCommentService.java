package Project.OpenBook.Domain.Description.Service;

import Project.OpenBook.Domain.Description.Repository.DescriptionRepository;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeywordRepository;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class DescriptionCommentService {

    private final DescriptionKeywordRepository descriptionKeywordRepository;
    private final DescriptionRepository descriptionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final KeywordRepository keywordRepository;

    private final ImageService imageService;
//
//    @Transactional(readOnly = true)
//    public DescriptionCommentDto queryQuestionDescription(Integer roundNumber, Integer questionNumber) {
//        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescription(roundNumber, questionNumber).orElseThrow(() -> {
//            throw new CustomException(QUESTION_NOT_FOUND);
//        });
//
//        Description description = examQuestion.getDescription();
//
//        List<DescriptionKeyword> descriptionKeywordList = descriptionKeywordRepository.queryDescriptionKeywordsForTopicList(description);
//
//        List<ExamQuestionDescQueryDto> dtoList = new ArrayList<>();
//        for (DescriptionKeyword descriptionKeyword : descriptionKeywordList) {
//            Keyword keyword = descriptionKeyword.getKeyword();
//            ExamQuestionDescQueryDto dto = new ExamQuestionDescQueryDto(keyword.getTopic().getChapter().getNumber(), keyword.getTopic().getTitle(),
//                    keyword.getName(), keyword.getId());
//            dtoList.add(dto);
//        }
//
//        return new DescriptionCommentDto(description.getContent(),description.getId(), dtoList);
//    }
//
//

}
