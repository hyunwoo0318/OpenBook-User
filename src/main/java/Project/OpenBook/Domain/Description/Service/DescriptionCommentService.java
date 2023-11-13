package Project.OpenBook.Domain.Description.Service;

import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeyword;
import Project.OpenBook.Domain.Description.Dto.DescriptionCommentAddDto;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Description.Dto.DescriptionUpdateDto;
import Project.OpenBook.Domain.DescriptionComment.DescriptionKeyword.DescriptionKeywordRepository;
import Project.OpenBook.Domain.Description.Repository.DescriptionRepository;
import Project.OpenBook.Domain.Description.Dto.DescriptionCommentDto;
import Project.OpenBook.Domain.Description.Dto.ExamQuestionDescQueryDto;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static Project.OpenBook.Constants.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class DescriptionCommentService {

    private final DescriptionKeywordRepository descriptionKeywordRepository;
    private final DescriptionRepository descriptionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final KeywordRepository keywordRepository;

    private final ImageService imageService;

    @Transactional(readOnly = true)
    public DescriptionCommentDto queryQuestionDescription(Integer roundNumber, Integer questionNumber) {
        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescription(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        Description description = examQuestion.getDescription();

        List<DescriptionKeyword> descriptionKeywordList = descriptionKeywordRepository.queryDescriptionKeywordsForTopicList(description);

        List<ExamQuestionDescQueryDto> dtoList = new ArrayList<>();
        for (DescriptionKeyword descriptionKeyword : descriptionKeywordList) {
            Keyword keyword = descriptionKeyword.getKeyword();
            ExamQuestionDescQueryDto dto = new ExamQuestionDescQueryDto(keyword.getTopic().getChapter().getNumber(), keyword.getTopic().getTitle(),
                    keyword.getName(), keyword.getId());
            dtoList.add(dto);
        }

        return new DescriptionCommentDto(description.getContent(),description.getId(), dtoList);
    }


    @Transactional
    public void insertDescriptionKeyword(Long id, DescriptionCommentAddDto dto) {
        Description description = checkDescription(id);

        //dto 검증
        Keyword keyword = checkKeyword(dto.getId());
        DescriptionKeyword descriptionKeyword = new DescriptionKeyword(description, keyword);
        descriptionKeywordRepository.save(descriptionKeyword);
    }

    private Description checkDescription(Long id) {
        return descriptionRepository.findById(id).orElseThrow(() -> {
            throw new CustomException(DESCRIPTION_NOT_FOUND);
        });
    }
    private Keyword checkKeyword(Long id) {
        return keywordRepository.findById(id).orElseThrow(() -> {
            throw new CustomException(KEYWORD_NOT_FOUND);
        });
    }

    @Transactional
    public void deleteDescriptionKeyword(Long id, DescriptionCommentAddDto dto) {
        Description description = checkDescription(id);
        //dto 검증
        Keyword keyword = checkKeyword(dto.getId());
        descriptionKeywordRepository.deleteByDescriptionAndKeyword(description, keyword);

    }

    @Transactional
    public void updateDescription(Long id, DescriptionUpdateDto dto) throws IOException {
        Description description = checkDescription(id);
        String encodedFile = dto.getDescription();
        String imageUrl="";

        if(encodedFile != null && !encodedFile.isBlank() &&!encodedFile.startsWith("https")){
            imageService.checkBase64(encodedFile);
            imageUrl = imageService.storeFile(encodedFile);
            description.updateContent(imageUrl, description.getComment());
        } else if (encodedFile == null || encodedFile.isBlank() || !encodedFile.startsWith("https")) {
            throw new CustomException(NOT_VALIDATE_IMAGE);
        }
    }
}
