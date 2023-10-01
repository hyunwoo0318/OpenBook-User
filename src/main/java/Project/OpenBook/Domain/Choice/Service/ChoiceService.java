package Project.OpenBook.Domain.Choice.Service;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Constants.CommentConst;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Choice.Domain.Choice;
import Project.OpenBook.Domain.Choice.Repository.ChoiceRepository;
import Project.OpenBook.Domain.ChoiceComment.*;
import Project.OpenBook.Domain.ExamQuestion.Domain.ExamQuestion;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.ExamQuestion.Service.dto.ChoiceAddUpdateDto;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yaml.snakeyaml.tokens.CommentToken;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ChoiceService {

    private final ChoiceRepository choiceRepository;
    private final ImageService imageService;
//    private final TopicRepository topicRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final ChoiceKeywordRepository choiceKeywordRepository;
    private final ChoiceSentenceRepository choiceSentenceRepository;
    private final KeywordRepository keywordRepository;
    private final SentenceRepository sentenceRepository;

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
    public void createChoice(Integer roundNumber, Integer questionNumber, ChoiceInfoDto dto) {
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
    public void updateChoice(Long choiceId, ChoiceInfoDto dto) {
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
    public void insertChoiceKeywordSentence(Long choiceId, ChoiceCommentAddUpdateDto dto) {
        Choice choice = choiceRepository.findById(choiceId).orElseThrow(() -> {
            throw new CustomException(CHOICE_NOT_FOUND);
        });

        String type = dto.getType();
        if (type.equals(CommentConst.KEYWORD)) {
            Keyword keyword = keywordRepository.findById(dto.getId()).orElseThrow(() -> {
                throw new CustomException(KEYWORD_NOT_FOUND);
            });
            ChoiceKeyword choiceKeyword = new ChoiceKeyword(choice, keyword);
            choiceKeywordRepository.save(choiceKeyword);
        } else if (type.equals(CommentConst.SENTENCE)) {
            Sentence sentence = sentenceRepository.findById(dto.getId()).orElseThrow(() -> {
                throw new CustomException(SENTENCE_NOT_FOUND);
            });
            choiceSentenceRepository.save(new ChoiceSentence(choice, sentence));

        }
    }

    @Transactional
    public void deleteChoiceKeywordSentence(Long choiceId, ChoiceCommentAddUpdateDto dto) {
        Choice choice = choiceRepository.findById(choiceId).orElseThrow(() -> {
            throw new CustomException(CHOICE_NOT_FOUND);
        });

        String type = dto.getType();
        if (type.equals(CommentConst.KEYWORD)) {
            Keyword keyword = keywordRepository.findById(dto.getId()).orElseThrow(() -> {
                throw new CustomException(KEYWORD_NOT_FOUND);
            });

            choiceKeywordRepository.deleteByChoiceAndKeyword(choice, keyword);
        } else if (type.equals(CommentConst.SENTENCE)) {
            Sentence sentence = sentenceRepository.findById(dto.getId()).orElseThrow(() -> {
                throw new CustomException(SENTENCE_NOT_FOUND);
            });
            choiceSentenceRepository.deleteByChoiceAndSentence(choice, sentence);

        }
    }

    @Transactional(readOnly = true)
    public List<ChoiceCommentQueryDto> queryQuestionChoices(Integer roundNumber, Integer questionNumber) {

        List<ChoiceCommentQueryDto> retList = new ArrayList<>();

        Map<Choice, List<ChoiceCommentInfoDto>> choiceKeywordMap
                = choiceKeywordRepository.queryChoiceKeywordsTemp(roundNumber, questionNumber);
        Map<Choice, List<ChoiceCommentInfoDto>> choiceSentenceMap
                = choiceSentenceRepository.queryChoiceSentenceTemp(roundNumber, questionNumber);

        Set<Choice> keywordSet = choiceKeywordMap.keySet();
        Set<Choice> sentenceSet = choiceSentenceMap.keySet();
        Set<Choice> totalChoiceSet = new HashSet<>();

        totalChoiceSet.addAll(keywordSet);
        totalChoiceSet.addAll(sentenceSet);

        for (Choice choice : totalChoiceSet) {
            List<ChoiceCommentInfoDto> commentList = new ArrayList<>();

            List<ChoiceCommentInfoDto> commentKeywordList = choiceKeywordMap.get(choice);
            List<ChoiceCommentInfoDto> commentSentenceList = choiceSentenceMap.get(choice);

            if (commentKeywordList != null) {
                commentList.addAll(commentKeywordList);
            }
            if (commentSentenceList != null) {
                commentList.addAll(commentSentenceList);
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
