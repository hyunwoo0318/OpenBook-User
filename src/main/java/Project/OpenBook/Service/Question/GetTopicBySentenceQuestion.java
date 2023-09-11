package Project.OpenBook.Service.Question;

import Project.OpenBook.Domain.Sentence;
import Project.OpenBook.Dto.question.QuestionChoiceDto;
import Project.OpenBook.Dto.question.QuestionDto;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.sentence.SentenceRepository;
import Project.OpenBook.Topic.Repo.TopicRepository;

import java.util.ArrayList;
import java.util.List;

import static Project.OpenBook.Constants.QuestionConst.GET_TOPIC_BY_SENTENCE_TYPE;
import static Project.OpenBook.Constants.QuestionConst.GET_TOPIC_WRONG_ANSWER_NUM;

public class GetTopicBySentenceQuestion extends BaseQuestionComponentFactory implements QuestionFactory{
    public GetTopicBySentenceQuestion(TopicRepository topicRepository, KeywordRepository keywordRepository, SentenceRepository sentenceRepository) {
        super(topicRepository, keywordRepository, sentenceRepository);
    }

    @Override
    public QuestionDto getQuestion(String topicTitle) {
        //정답 문장 조회
        List<Sentence> answerSentence = getSentencesByAnswerTopic(topicTitle, 1);
        if (answerSentence.isEmpty()) {
            return toQuestionDto(topicTitle, "", new ArrayList<>());
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
