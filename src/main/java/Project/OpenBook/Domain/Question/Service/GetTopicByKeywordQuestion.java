package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Question.Dto.QuizChoiceDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.WeightedRandomSelection.Model.KeywordSelectModel;
import Project.OpenBook.WeightedRandomSelection.WeightedRandomService;

import java.util.*;

import static Project.OpenBook.Constants.QuestionConst.GET_TOPIC_BY_KEYWORD_TYPE;

public class GetTopicByKeywordQuestion extends BaseQuestionComponentFactory implements QuestionFactory {
    public GetTopicByKeywordQuestion(TopicRepository topicRepository, KeywordRepository keywordRepository,
                                     WeightedRandomService weightedRandomService) {
        super(topicRepository, keywordRepository, weightedRandomService);
    }

    @Override
    public List<QuestionDto> getQuestion(Map<Keyword, KeywordLearningRecord> keywordRecordMap,
                                         List<Keyword> totalKeywordList, Integer questionCount){
        List<QuestionDto> questionList = new ArrayList<>();

        while(questionList.size() != questionCount){

            //2. 정답 키워드 선정
            List<KeywordSelectModel> answerKeywordSelectModelList
                    = makeAnswerKeywordSelectModelList(keywordRecordMap, totalKeywordList);
            Keyword answerKeyword = selectAnswerKeyword(answerKeywordSelectModelList);
            Topic answerTopic = answerKeyword.getTopic();
            //TODO : answerKeyword와 같은 topic을 가진 키워드 1개 조회
            Keyword answerKeyword2 = null;

            //3. 오답 주제 선정
            //TODO : 미리 한번에 꺼내둬서 map으로 뽑아쓰기
            List<Topic> wrongTopicList = getWrongTopic(answerTopic, 3);

            QuestionDto questionDto = toQuestionDto(answerTopic, Arrays.asList(answerKeyword, answerKeyword2), wrongTopicList);
            questionList.add(questionDto);
        }
        return  questionList;
    }

    public List<QuestionDto> getJJHQuestion(List<Topic> topicList) {
        List<QuestionDto> questionList = new ArrayList<>();
        //TODO : 전체 토픽에 대해서 가능한 모든 keywordList 꺼내오기
        Map<Topic, List<Topic>> totalWrongKeywordMap = new HashMap<>();

        //각 토픽에 대해서 문제 생성
        for (Topic answerTopic : topicList) {
            List<Keyword> answerTotalKeywordList = answerTopic.getKeywordList();
            List<Keyword> answerKeywordList = new ArrayList<>();
            List<Topic> wrongTopicList = new ArrayList<>();
            //1. 2개의 정답 키워드를 랜덤하게 선정
            Set<Integer> randomIndex = getRandomIndex(2, answerTotalKeywordList.size());
            for (Integer index : randomIndex) {
                answerKeywordList.add(answerTotalKeywordList.get(index));
            }

            //2. 3개의 오답 주제 선정 -> questionCategory는 같은경우
            //TODO : 변경사항 -> map에서 꺼내서 사용하기
            wrongTopicList = getWrongTopic(answerTopic, 3);

            //3. dto변환
            QuestionDto question = toQuestionDto(answerTopic, answerKeywordList, wrongTopicList);
            questionList.add(question);
        }
        return questionList;
    }


    private QuestionDto toQuestionDto(Topic answerTopic, List<Keyword> answerKeywordList, List<Topic> wrongTopicList) {
        List<QuizChoiceDto> choiceList = new ArrayList<>();
        List<Long> keywordList = new ArrayList<>();
        List<String> descriptionList = new ArrayList<>();
        String topicTitle = answerTopic.getTitle();
        choiceList.add(new QuizChoiceDto(topicTitle, topicTitle));
        for (Keyword keyword : answerKeywordList) {
            keywordList.add(keyword.getId());
            descriptionList.add(keyword.getName());
        }
        wrongTopicList.forEach(t -> choiceList.add(new QuizChoiceDto(t.getTitle(), t.getTitle())));

        return QuestionDto.builder()
                .questionType(GET_TOPIC_BY_KEYWORD_TYPE)
                .choiceType(ChoiceType.String.name())
                .answer(topicTitle)
                .choiceList(choiceList)
                .description(descriptionList)
                .keywordIdList(keywordList)
                .build();
    }


    private QuestionDto toQuestionDto(String topicTitle, List<String> descriptionList, List<Long> keywordIdList, List<QuizChoiceDto> choiceList) {
        return QuestionDto.builder()
                .questionType(GET_TOPIC_BY_KEYWORD_TYPE)
                .answer(topicTitle)
                .choiceList(choiceList)
                .description(descriptionList)
                .keywordIdList(keywordIdList)
                .build();
    }
}
