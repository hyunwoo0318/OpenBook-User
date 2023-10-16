package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Question.Dto.ChoiceTempDto;
import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static Project.OpenBook.Constants.QuestionConst.GET_KEYWORD_TYPE;
import static Project.OpenBook.Constants.QuestionConst.WRONG_KEYWORD_NUM;

/**
 * 주제 보고 키워드 맞추기
 */
public class GetKeywordByTopicQuestion extends BaseQuestionComponentFactory implements QuestionFactory {

    public GetKeywordByTopicQuestion(TopicRepository topicRepository, KeywordRepository keywordRepository) {
        super(topicRepository, keywordRepository);
    }

    /**
     * 해당 토픽에 존재하는 1개의 키워드를 랜덤하게 골라서 문제를 만드는 메서드
     * 고려해야할 예외상황
     *      1. 해당 토픽에 키워드가 존재하지 않는 경우 -> null return
     *      2. 오답 키워드가 1개도 존재하지 않는 경우 -> null return
     * @param topicTitle
     * @return
     */
    @Override
    public QuestionDto getQuestion(String topicTitle) {
        List<ChoiceTempDto> choiceList = new ArrayList<>();
        //정답 키워드 조회
        List<Keyword> answerKeywordList = getKeywordsByAnswerTopic(topicTitle, 1);
        if (answerKeywordList.isEmpty()) {
            return null;
        }
        List<ChoiceTempDto> answerChoiceList = toQuestionChoiceDtoByKeyword(answerKeywordList);

        //오답 키워드 조회
        List<ChoiceTempDto> wrongChoiceList = getWrongKeywordsByTopic(topicTitle, WRONG_KEYWORD_NUM);
        if (wrongChoiceList.isEmpty()) {
            return null;
        }

        //Dto 변환
        choiceList.addAll(answerChoiceList);
        choiceList.addAll(wrongChoiceList);
        return toQuestionDto(topicTitle, choiceList);
    }

    /**
     * JJH question -> 해당 토픽의 전체 키워드를 가지고 문제를 1개씩 만듬
     * 고려해야할 예외상황
     *     1. 해당 토픽에 키워드가 하나도 없는 경우 -> 빈 List를 리턴함
     *     2. 다른 토픽에 키워드가 1개도 존재하지 않는 경우 -> 빈 List를 생성함
     * @param topicTitle
     * @return
     */
    public List<QuestionDto> getJJHQuestion(String topicTitle) {
        List<QuestionDto> questionList = new ArrayList<>();
        List<Keyword> keywordList = getTotalKeywordByAnswerTopic(topicTitle);
        for (Keyword k : keywordList) {
            //오답 키워드 조회
            List<ChoiceTempDto> choiceList = getWrongKeywordsByTopic(topicTitle, WRONG_KEYWORD_NUM);

            //Dto 변환
            if(!choiceList.isEmpty()){
                choiceList.add(new ChoiceTempDto(k.getName(), topicTitle));
                QuestionDto dto = toQuestionDto(topicTitle, choiceList);
                questionList.add(dto);
            }
        }
        return questionList;
    }

    private QuestionDto toQuestionDto(String topicTitle, List<ChoiceTempDto> choiceList) {
        return QuestionDto.builder()
                .questionType(GET_KEYWORD_TYPE)
                .choiceType(ChoiceType.String.name())
                .description(Arrays.asList(topicTitle))
                .answer(topicTitle)
                .choiceList(choiceList)
                .build();
    }

}
