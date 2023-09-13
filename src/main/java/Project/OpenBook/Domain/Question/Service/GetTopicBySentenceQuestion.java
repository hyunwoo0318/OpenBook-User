package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;

import java.util.ArrayList;
import java.util.List;

import static Project.OpenBook.Constants.QuestionConst.GET_TOPIC_BY_SENTENCE_TYPE;
import static Project.OpenBook.Constants.QuestionConst.GET_TOPIC_WRONG_ANSWER_NUM;

public class GetTopicBySentenceQuestion extends BaseQuestionComponentFactory implements QuestionFactory{
    public GetTopicBySentenceQuestion(TopicRepository topicRepository, KeywordRepository keywordRepository, SentenceRepository sentenceRepository) {
        super(topicRepository, keywordRepository, sentenceRepository);
    }

    /**
     * 해당 토픽에서 문장 1개를 보여주고 토픽 제목을 맞추는 문제
     * 고려해야할 예외상황
     *      1. 정답 문장이 존재하지 않는 경우 -> return null
     * @param topicTitle
     * @return
     */
    @Override
    public QuestionDto getQuestion(String topicTitle) {
        //정답 문장 조회
        List<Sentence> answerSentence = getSentencesByAnswerTopic(topicTitle, 1);
        if (answerSentence.isEmpty()) {
            return null;
        }
        String answerSentenceName = answerSentence.get(0).getName();

        //오답 선지 조회
        List<QuestionChoiceDto> choiceList = getWrongTopic(topicTitle, GET_TOPIC_WRONG_ANSWER_NUM);

        //정답 선지 추가
        choiceList.add(new QuestionChoiceDto(topicTitle, null, topicTitle));

        //Dto 변환
        return toQuestionDto(topicTitle, answerSentenceName, choiceList);
    }

    private QuestionDto toQuestionDto(String topicTitle, String answerSentenceName, List<QuestionChoiceDto> choiceList) {
        return QuestionDto.builder()
                .questionType(GET_TOPIC_BY_SENTENCE_TYPE)
                .answer(topicTitle)
                .choiceList(choiceList)
                .descriptionSentence(answerSentenceName)
                .build();
    }
}
