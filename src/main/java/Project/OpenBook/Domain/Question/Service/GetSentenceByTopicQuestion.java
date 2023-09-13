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

    /**
     * 해당 토픽에서 랜덤하게 1개의 문장을 골라서 문제를 생성
     * 고려해야할 예외상황
     *      1. 해당 토픽에서 문장이 없는 경우 -> null return
     *      2. 문장이 아예 존재하지 않는 경우 -> null return
     * @param topicTitle
     * @return
     */
    @Override
    public QuestionDto getQuestion(String topicTitle) {
        List<QuestionChoiceDto> choiceList = new ArrayList<>();
        //정답 문장 조회
        List<Sentence> answerSentence = getSentencesByAnswerTopic(topicTitle, 1);
        if (answerSentence.isEmpty()) {
            return null;
        }
        List<QuestionChoiceDto> answerChoice = toQuestionChoiceDtoBySentence(answerSentence);
        choiceList.addAll(answerChoice);

        //오답 문장 조회
        List<QuestionChoiceDto> wrongChoiceList = getWrongSentencesByTopic(topicTitle, WRONG_KEYWORD_SENTENCE_NUM);
        if (wrongChoiceList.isEmpty()) {
            return null;
        }
        choiceList.addAll(wrongChoiceList);

        //Dto 변환
        return toQuestionDto(topicTitle,choiceList);
    }

    /**
     * jjh 문제 생성 -> 토픽에 존재하는 모든 문장을 가지고 1개씩 문제를 생성
     * 고려해야할 예외상황
     *      1. 해당 토픽에 문장이 아예 존재하지 않을 경우 -> 빈 리스트 리턴
     *      2. 전체 문장이 아예 없을경우 -> 빈 리스트 리턴
     * @param topicTitle
     * @return
     */
    public List<QuestionDto> getJJHQuestion(String topicTitle) {
        List<QuestionDto> questionList = new ArrayList<>();
        List<Sentence> sentenceList = getTotalSentenceByAnswerTopic(topicTitle);
        for (Sentence sentence : sentenceList) {
            //오답 문장 조회
            List<QuestionChoiceDto> choiceList = getWrongSentencesByTopic(topicTitle, WRONG_KEYWORD_SENTENCE_NUM);


            if(!choiceList.isEmpty()){
                //정답 문장 추가
                choiceList.add(new QuestionChoiceDto(sentence.getName(), null, topicTitle));

                //Dto 변환
                QuestionDto dto = toQuestionDto(topicTitle, choiceList);
                questionList.add(dto);
            }
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
