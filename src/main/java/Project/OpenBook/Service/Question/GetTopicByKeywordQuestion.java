package Project.OpenBook.Service.Question;

import Project.OpenBook.Constants.QuestionConst;
import Project.OpenBook.Dto.keyword.KeywordNameCommentDto;
import Project.OpenBook.Dto.question.QuestionChoiceDto;
import Project.OpenBook.Dto.question.QuestionDto;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.sentence.SentenceRepository;
import Project.OpenBook.Topic.Repo.TopicRepository;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.QuestionConst.GET_TOPIC_BY_KEYWORD_TYPE;

public class GetTopicByKeywordQuestion extends BaseQuestionComponentFactory implements QuestionFactory {
    public GetTopicByKeywordQuestion(TopicRepository topicRepository, KeywordRepository keywordRepository, SentenceRepository sentenceRepository) {
        super(topicRepository, keywordRepository, sentenceRepository);
    }

    @Override
    public QuestionDto getQuestion(String topicTitle) {
        //정답 키워드 2개 조회
        List<KeywordNameCommentDto> descriptionKeywordList = getKeywordsByAnswerTopic(topicTitle, 2).stream()
                .map(k -> new KeywordNameCommentDto(k.getName(), k.getComment()))
                .collect(Collectors.toList());

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
