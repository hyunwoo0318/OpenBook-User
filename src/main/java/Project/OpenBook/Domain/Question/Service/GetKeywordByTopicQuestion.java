package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Question.Dto.QuizChoiceDto;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.WeightedRandomSelection.Model.AnswerKeywordSelectModel;
import Project.OpenBook.Domain.WeightedRandomSelection.Model.TopicSelectModel;
import Project.OpenBook.Domain.WeightedRandomSelection.WeightedRandomService;
import Project.OpenBook.Domain.WeightedRandomSelection.Model.WrongKeywordSelectModel;

import java.util.*;

import static Project.OpenBook.Constants.QuestionConst.GET_KEYWORD_TYPE;
import static Project.OpenBook.Constants.QuestionConst.WRONG_KEYWORD_NUM;

/**
 * 주제 보고 키워드 맞추기
 */
public class GetKeywordByTopicQuestion extends BaseQuestionComponentFactory implements QuestionFactory {

    public GetKeywordByTopicQuestion(TopicRepository topicRepository, KeywordRepository keywordRepository,
                                     WeightedRandomService weightedRandomService) {
        super(topicRepository, keywordRepository, weightedRandomService);
    }


    @Override
    public List<QuestionDto> getQuestion(Map<Topic, TopicLearningRecord> topicRecordMap, Map<Keyword, KeywordLearningRecord> keywordRecordMap,
                                         QuestionCategory questionCategory, Integer questionCount) {
        List<QuestionDto> questionList = new ArrayList<>();

        List<TopicSelectModel> topicSelectModelList = makeTopicSelectModelList(topicRecordMap, questionCategory);
        for (int i = 0; i < questionCount; i++) {
            //1. 정답 주제 선정
            Topic answerTopic = selectAnswerTopic(topicSelectModelList);

            //2. 정답 키워드 선정
            List<AnswerKeywordSelectModel> answerKeywordSelectModelList
                    = makeAnswerKeywordSelectModelList(keywordRecordMap, answerTopic);
            List<Keyword> answerKeywordList = selectAnswerKeywordList(answerKeywordSelectModelList, 1);

            //3. 오답 키워드 선정
            List<WrongKeywordSelectModel> wrongKeywordSelectModelList = makeWrongKeywordSelectModelList(keywordRecordMap, answerTopic, answerKeywordList);
            List<Keyword> wrongKeywordList = selectWrongKeywordList(wrongKeywordSelectModelList, 3);

            QuestionDto questionDto = toQuestionDto(answerTopic, answerKeywordList, wrongKeywordList);
            questionList.add(questionDto);
        }

        return questionList;

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
        List<Keyword> totalWrongKeywordList = getTotalWrongKeywords(topicTitle);
        int wrongKeywordListSize = totalWrongKeywordList.size();
        for (Keyword k : keywordList) {
            List<QuizChoiceDto> choiceList = new ArrayList<>();

            //랜덤하게 3개의 숫자 골라서 저장
            Set<Integer> idxSet = getRandomIndex(WRONG_KEYWORD_NUM, wrongKeywordListSize);
            for (Integer idx : idxSet) {
                Keyword keyword = totalWrongKeywordList.get(idx);
                choiceList.add(new QuizChoiceDto(keyword.getName(), keyword.getTopic().getTitle()));
            }

            //Dto 변환
            if(!choiceList.isEmpty()){
                choiceList.add(new QuizChoiceDto(k.getName(), k.getTopic().getTitle()));
                QuestionDto dto = toQuestionDto(topicTitle, choiceList, Arrays.asList(k.getId()));
                questionList.add(dto);
            }
        }
        return questionList;
    }

    private QuestionDto toQuestionDto(Topic answerTopic, List<Keyword> answerKeywordList, List<Keyword> wrongKeywordList) {
        List<QuizChoiceDto> choiceList = new ArrayList<>();
        List<Long> keywordList = new ArrayList<>();

        String topicTitle = answerTopic.getTitle();

        for (Keyword keyword : answerKeywordList) {
            choiceList.add(new QuizChoiceDto(keyword.getName(), keyword.getTopic().getTitle()));
            keywordList.add(keyword.getId());
        }

        wrongKeywordList.forEach(k -> choiceList.add(new QuizChoiceDto(k.getName(), k.getTopic().getTitle())));
        return QuestionDto.builder()
                .questionType(GET_KEYWORD_TYPE)
                .choiceType(ChoiceType.String.name())
                .description(Arrays.asList(topicTitle))
                .answer(topicTitle)
                .choiceList(choiceList)
                .keywordIdList(keywordList)
                .build();
    }

    private QuestionDto toQuestionDto(String topicTitle, List<QuizChoiceDto> quizChoiceList, List<Long> keywordIdList) {

        return QuestionDto.builder()
                .questionType(GET_KEYWORD_TYPE)
                .choiceType(ChoiceType.String.name())
                .description(Arrays.asList(topicTitle))
                .answer(topicTitle)
                .choiceList(quizChoiceList)
                .keywordIdList(keywordIdList)
                .build();
    }

}
