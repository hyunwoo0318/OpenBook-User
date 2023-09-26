package Project.OpenBook.Domain.Description.Service;

import Project.OpenBook.Domain.Description.Controller.DescriptionCommentAddDto;
import Project.OpenBook.Domain.Description.Domain.Description;
import Project.OpenBook.Domain.Description.Dto.DescriptionUpdateDto;
import Project.OpenBook.Domain.Description.Repository.DescriptionKeywordRepository;
import Project.OpenBook.Domain.Description.Repository.DescriptionRepository;
import Project.OpenBook.Domain.DescriptionComment.DescriptionCommentDto;
import Project.OpenBook.Domain.DescriptionComment.ExamQuestionDescQueryDto;
import Project.OpenBook.Domain.DescriptionSentence.DescriptionSentence;
import Project.OpenBook.Domain.DescriptionSentence.DescriptionSentenceRepository;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static Project.OpenBook.Constants.CommentConst.KEYWORD;
import static Project.OpenBook.Constants.CommentConst.SENTENCE;
import static Project.OpenBook.Constants.ErrorCode.*;

@RequiredArgsConstructor
@Service
public class DescriptionCommentService {

    private final DescriptionKeywordRepository descriptionKeywordRepository;
    private final DescriptionSentenceRepository descriptionSentenceRepository;
    private final DescriptionRepository descriptionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final KeywordRepository keywordRepository;
    private final SentenceRepository sentenceRepository;

    @Transactional(readOnly = true)
    public DescriptionCommentDto queryQuestionDescription(Integer roundNumber, Integer questionNumber) {
        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestionWithDescription(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        Description description = examQuestion.getDescription();

        List<DescriptionKeyword> descriptionKeywordList = descriptionKeywordRepository.queryDescriptionKeywords(description);
        List<DescriptionSentence> descriptionSentenceList = descriptionSentenceRepository.queryDescriptionSentences(description);

        List<ExamQuestionDescQueryDto> dtoList = new ArrayList<>();
        for (DescriptionKeyword descriptionKeyword : descriptionKeywordList) {
            Keyword keyword = descriptionKeyword.getKeyword();
            ExamQuestionDescQueryDto dto = new ExamQuestionDescQueryDto(keyword.getTopic().getChapter().getNumber(), keyword.getTopic().getTitle(),
                    KEYWORD, keyword.getName(), keyword.getId());
            dtoList.add(dto);
        }

        for (DescriptionSentence descriptionSentence : descriptionSentenceList) {
            Sentence sentence = descriptionSentence.getSentence();
            ExamQuestionDescQueryDto dto = new ExamQuestionDescQueryDto(sentence.getTopic().getChapter().getNumber(), sentence.getTopic().getTitle(),
                    SENTENCE, sentence.getName(), sentence.getId());
            dtoList.add(dto);
        }
        return new DescriptionCommentDto(description.getContent(),description.getId(), dtoList);
    }


    @Transactional
    public void insertDescriptionKeywordSentence(Long id, DescriptionCommentAddDto dto) {
        Description description = checkDescription(id);

        //dto 검증
        if(dto.getType().equals(KEYWORD)){
            Keyword keyword = checkKeyword(dto.getId());
            DescriptionKeyword descriptionKeyword = new DescriptionKeyword(description, keyword);
            descriptionKeywordRepository.save(descriptionKeyword);
        }else if(dto.getType().equals(SENTENCE)){
            Sentence sentence = checkSentence(dto.getId());
            DescriptionSentence descriptionSentence = new DescriptionSentence(description, sentence);
            descriptionSentenceRepository.save(descriptionSentence);
        }else{
            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
        }
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

    private Sentence checkSentence(Long id) {
        return sentenceRepository.findById(id).orElseThrow(() -> {
            throw new CustomException(SENTENCE_NOT_FOUND);
        });
    }

    @Transactional
    public void deleteDescriptionKeywordSentence(Long id, DescriptionCommentAddDto dto) {
        Description description = checkDescription(id);
        //dto 검증
        if(dto.getType().equals(KEYWORD)){
            Keyword keyword = checkKeyword(dto.getId());
            descriptionKeywordRepository.deleteByDescriptionAndKeyword(description, keyword);
        }else if(dto.getType().equals(SENTENCE)){
            Sentence sentence = checkSentence(dto.getId());
            descriptionSentenceRepository.deleteByDescriptionAndSentence(description, sentence);
        }else{
            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
        }
    }

    @Transactional
    public void updateDescription(Long id, DescriptionUpdateDto dto) {
        Description description = checkDescription(id);

        description.updateContent(dto.getDescription(), description.getComment());
    }
}
