package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Constants.QuestionConst;
import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Keyword.Dto.KeywordNameCommentDto;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.QuestionConst.GET_TOPIC_BY_KEYWORD_TYPE;

public class GetTopicByKeywordQuestion extends BaseQuestionComponentFactory implements QuestionFactory {
    public GetTopicByKeywordQuestion(TopicRepository topicRepository, KeywordRepository keywordRepository, SentenceRepository sentenceRepository) {
        super(topicRepository, keywordRepository, sentenceRepository);
    }

    /**
     * 해당 토픽에서 키워드 2개를 보여주고 토픽 제목을 맞추는 문제
     * 고려해야할 예외상황
     *      1. 정답 키워드가 2개이상 존재하지 않는 경우 -> return null
     * @param topicTitle
     * @return
     */
    @Override
    public QuestionDto getQuestion(String topicTitle) {
        //정답 키워드 2개 조회
        List<KeywordNameCommentDto> descriptionKeywordList = getKeywordsByAnswerTopic(topicTitle, 2).stream()
                .map(k -> new KeywordNameCommentDto(k.getName(), k.getComment()))
                .collect(Collectors.toList());

        if (descriptionKeywordList.size() != 2) {
            return null;
        }

        //오답 주제 조회
        List<QuestionChoiceDto> choiceList = getWrongTopic(topicTitle, QuestionConst.GET_TOPIC_WRONG_ANSWER_NUM);

        //정답 선지 추가
        choiceList.add(new QuestionChoiceDto(topicTitle, null, topicTitle));

        //Dto 변환
        return toQuestionDto(topicTitle, descriptionKeywordList, choiceList);
    }

    private QuestionDto toQuestionDto(String topicTitle, List<KeywordNameCommentDto> descriptionKeywordList, List<QuestionChoiceDto> choiceList) {
               return QuestionDto.builder()
                .questionType(GET_TOPIC_BY_KEYWORD_TYPE)
                .answer(topicTitle)
                .choiceList(choiceList)
                .descriptionKeyword(descriptionKeywordList)
                .build();
    }
}
