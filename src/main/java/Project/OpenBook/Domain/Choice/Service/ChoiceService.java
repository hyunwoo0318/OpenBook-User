package Project.OpenBook.Domain.Choice.Service;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeyword;
import Project.OpenBook.Domain.ChoiceComment.ChoiceKeyword.ChoiceKeywordRepository;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceCommentAddUpdateDto;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceCommentInfoDto;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceCommentQueryDto;
import Project.OpenBook.Domain.ChoiceComment.Service.Dto.ChoiceInfoDto;
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
import java.util.*;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final ChoiceRepository choiceRepository;
    private final ImageService imageService;
    private final ExamQuestionRepository examQuestionRepository;
    private final ChoiceKeywordRepository choiceKeywordRepository;
    private final KeywordRepository keywordRepository;

//    @Transactional
//    public void updateChoice(Long choiceId, ChoiceAddUpdateDto dto) throws IOException {
//        Choice choice = choiceRepository.findById(choiceId).orElseThrow(() -> {
//            throw new CustomException(CHOICE_NOT_FOUND);
//        });
//
//        Topic topic = checkTopic(dto.getKey());
//
//        String inputChoiceType = dto.getChoiceType();
//
//        //입력받은 choiceType이 옳은 형식인지 확인
//        ChoiceType choiceType = checkChoiceType(inputChoiceType);
//
//        if(choiceType.equals(ChoiceType.String)){
//            choice.updateChoice(dto.getChoice(), dto.getComment(), topic);
//        }
//        //선지 저장(이미지)
//        else if(choiceType.equals(ChoiceType.Image)){
//            String encodedFile = dto.getChoice();
//            String choiceUrl = choice.getContent();
//            if (!encodedFile.startsWith("https")){
//                imageService.checkBase64(encodedFile);
//                choiceUrl = imageService.storeFile(encodedFile);
//            }
//            choice.updateChoice(choiceUrl, dto.getComment(), topic);
//        }else{
//            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
//        }
//    }
//
//    @Transactional
//    public void deleteChoice(Long choiceId) {
//        Choice choice = choiceRepository.findById(choiceId).orElseThrow(() -> {
//            throw new CustomException(CHOICE_NOT_FOUND);
//        });
//
//        choiceRepository.delete(choice);
//    }

//    private Topic checkTopic(String topicTitle) {
//        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
//            throw new CustomException(TOPIC_NOT_FOUND);
//        });
//    }

    @Transactional
    public void createChoice(Integer roundNumber, Integer questionNumber, ChoiceInfoDto dto) throws IOException {
        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestion(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        //예외처리
        String inputChoiceType = dto.getChoiceType();
        ChoiceType choiceType = checkChoiceType(inputChoiceType);
        String content = dto.getChoice();
        if (choiceType.equals(ChoiceType.Image)) {
            imageService.checkBase64(content);
            content = imageService.storeFile(content);
        }
        Choice choice = new Choice(dto.getChoiceNumber(), content,  choiceType, examQuestion);
        choiceRepository.save(choice);
    }

    @Transactional
    public void updateChoice(Long choiceId, ChoiceInfoDto dto) throws IOException {
        Choice choice = choiceRepository.findById(choiceId).orElseThrow(() -> {
            throw new CustomException(CHOICE_NOT_FOUND);
        });

        String inputChoiceType = dto.getChoiceType();

        //입력받은 choiceType이 옳은 형식인지 확인
        ChoiceType choiceType = checkChoiceType(inputChoiceType);

        if(choiceType.equals(ChoiceType.String)){
            choice.updateChoice(dto.getChoiceNumber(), dto.getChoice());
        }
        //선지 저장(이미지)
        else if(choiceType.equals(ChoiceType.Image)){
            String encodedFile = dto.getChoice();
            if (!encodedFile.startsWith("https")){
                imageService.checkBase64(encodedFile);
                encodedFile = imageService.storeFile(encodedFile);
            }
            choice.updateChoice(dto.getChoiceNumber(),encodedFile);
        }else{
            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
        }
    }

    @Transactional
    public void insertChoiceKeyword(Long choiceId, ChoiceCommentAddUpdateDto dto) {
        Choice choice = choiceRepository.findById(choiceId).orElseThrow(() -> {
            throw new CustomException(CHOICE_NOT_FOUND);
        });


            Keyword keyword = keywordRepository.findById(dto.getId()).orElseThrow(() -> {
                throw new CustomException(KEYWORD_NOT_FOUND);
            });
            ChoiceKeyword choiceKeyword = new ChoiceKeyword(choice, keyword);
            choiceKeywordRepository.save(choiceKeyword);


    }

    @Transactional
    public void deleteChoiceKeyword(Long choiceId, ChoiceCommentAddUpdateDto dto) {
        Choice choice = choiceRepository.findById(choiceId).orElseThrow(() -> {
            throw new CustomException(CHOICE_NOT_FOUND);
        });



            Keyword keyword = keywordRepository.findById(dto.getId()).orElseThrow(() -> {
                throw new CustomException(KEYWORD_NOT_FOUND);
            });

            choiceKeywordRepository.deleteByChoiceAndKeyword(choice, keyword);

    }

    @Transactional(readOnly = true)
    public List<ChoiceCommentQueryDto> queryQuestionChoices(Integer roundNumber, Integer questionNumber) {

        List<ChoiceCommentQueryDto> retList = new ArrayList<>();
        ExamQuestion examQuestion = examQuestionRepository.queryExamQuestion(roundNumber, questionNumber).orElseThrow(() -> {
            throw new CustomException(QUESTION_NOT_FOUND);
        });

        List<Choice> choiceList = examQuestion.getChoiceList();
        Map<Choice, List<ChoiceCommentInfoDto>> choiceKeywordMap = choiceKeywordRepository.queryChoiceKeywordsForAdmin(choiceList);

        for (Choice choice : choiceList) {
            List<ChoiceCommentInfoDto> commentList = new ArrayList<>();
            List<ChoiceCommentInfoDto> dtoKeywordList = choiceKeywordMap.get(choice);
            if (dtoKeywordList != null) {
                commentList.addAll(dtoKeywordList);
            }

            retList.add(new ChoiceCommentQueryDto(choice.getContent(), choice.getNumber(), choice.getId(),
                    choice.getType().name(), commentList));
        }


        return retList;
    }

    private ChoiceType checkChoiceType(String inputChoiceType){
        //입력받은 choiceType이 옳은 형식인지 확인
        Map<String, ChoiceType> map = ChoiceType.getChoiceTypeNameMap();
        ChoiceType choiceType = map.get(inputChoiceType);
        if(choiceType == null){
            throw new CustomException(NOT_VALIDATE_CHOICE_TYPE);
        }
        return choiceType;
    }

}
