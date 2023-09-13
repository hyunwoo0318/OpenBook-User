package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;

import java.util.ArrayList;
import java.util.List;

import static Project.OpenBook.Constants.QuestionConst.GET_SENTENCE_TYPE;
import static Project.OpenBook.Constants.QuestionConst.WRONG_KEYWORD_SENTENCE_NUM;

public class GetSentenceByTopicQuestion extends BaseQuestionComponentFactory implements QuestionFactory {
    public GetSentenceByTopicQuestion(TopicRepository topicRepository, KeywordRepository keywordRepository, SentenceRepository sentenceRepository) {
        super(topicRepository, keywordRepository, sentenceRepository);
    }

    @Override
    public QuestionDto getQuestion(String topicTitle) {
        List<QuestionChoiceDto> choiceList = new ArrayList<>();
        //정답 문장 조회
        List<Sentence> answerSentence = getSentencesByAnswerTopic(topicTitle, 1);
        List<QuestionChoiceDto> answerChoice = toQuestionChoiceDtoBySentence(answerSentence);
        choiceList.addAll(answerChoice);

        //오답 문장 조회
        List<QuestionChoiceDto> wrongChoiceList = getWrongSentencesByTopic(topicTitle, WRONG_KEYWORD_SENTENCE_NUM);
        choiceList.addAll(wrongChoiceList);

        //Dto 변환
        return toQuestionDto(topicTitle,choiceList);
    }

    public List<QuestionDto> getJJHQuestion(String topicTitle) {
        List<QuestionDto> questionList = new ArrayList<>();
        List<Sentence> sentenceList = getTotalSentenceByAnswerTopic(topicTitle);
        for (Sentence sentence : sentenceList) {
            //오답 문장 조회
            List<QuestionChoiceDto> choiceList = getWrongSentencesByTopic(topicTitle, WRONG_KEYWORD_SENTENCE_NUM);

            //정답 문장 추가
            choiceList.add(new QuestionChoiceDto(sentence.getName(), null, topicTitle));

            //Dto 변환
            QuestionDto dto = toQuestionDto(topicTitle, choiceList);
            questionList.add(dto);
        }
        return questionList;
    }

    private QuestionDto toQuestionDto(String topicTitle, List<QuestionChoiceDto> choiceList) {
        return QuestionDto.builder()
                .questionType(GET_SENTENCE_TYPE)
                .descriptionSentence(topicTitle)
                .answer(topicTitle)
                .choiceList(choiceList)
                .build();
    }
}
