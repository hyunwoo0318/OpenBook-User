package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;

import java.util.ArrayList;
import java.util.List;

import static Project.OpenBook.Constants.QuestionConst.GET_KEYWORD_TYPE;
import static Project.OpenBook.Constants.QuestionConst.WRONG_KEYWORD_SENTENCE_NUM;

/**
 * 주제 보고 키워드 맞추기
 */
public class GetKeywordByTopicQuestion extends BaseQuestionComponentFactory implements QuestionFactory {

    public GetKeywordByTopicQuestion(TopicRepository topicRepository, KeywordRepository keywordRepository, SentenceRepository sentenceRepository) {
        super(topicRepository, keywordRepository, sentenceRepository);
    }

    @Override
    public QuestionDto getQuestion(String topicTitle) {
        List<QuestionChoiceDto> choiceList = new ArrayList<>();
        //정답 키워드 조회
        List<Keyword> answerKeywordList = getKeywordsByAnswerTopic(topicTitle, 1);
        List<QuestionChoiceDto> answerChoiceList = toQuestionChoiceDtoByKeyword(answerKeywordList);

        //오답 키워드 조회
        List<QuestionChoiceDto> wrongChoiceList = getWrongKeywordsByTopic(topicTitle, WRONG_KEYWORD_SENTENCE_NUM);

        //Dto 변환
        choiceList.addAll(answerChoiceList);
        choiceList.addAll(wrongChoiceList);
        return toQuestionDto(topicTitle, choiceList);
    }

    public List<QuestionDto> getJJHQuestion(String topicTitle) {
        List<QuestionDto> questionList = new ArrayList<>();
        List<Keyword> keywordList = getTotalKeywordByAnswerTopic(topicTitle);
        for (Keyword k : keywordList) {
            //오답 키워드 조회
            List<QuestionChoiceDto> choiceList = getWrongKeywordsByTopic(topicTitle, WRONG_KEYWORD_SENTENCE_NUM);

            //Dto 변환
            choiceList.add(new QuestionChoiceDto(k.getName(), k.getComment(), topicTitle));
            QuestionDto dto = toQuestionDto(topicTitle, choiceList);
            questionList.add(dto);
        }
        return questionList;
    }

    private QuestionDto toQuestionDto(String topicTitle, List<QuestionChoiceDto> choiceList) {
        return QuestionDto.builder()
                .questionType(GET_KEYWORD_TYPE)
                .descriptionSentence(topicTitle)
                .answer(topicTitle)
                .choiceList(choiceList)
                .build();
    }

}
