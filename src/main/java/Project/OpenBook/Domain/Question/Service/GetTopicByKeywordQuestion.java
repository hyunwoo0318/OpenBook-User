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
import Project.OpenBook.WeightedRandomSelection.Model.KeywordSelectModel;
import Project.OpenBook.WeightedRandomSelection.WeightedRandomService;

import java.util.*;
import java.util.stream.Collectors;

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

        Set<Topic> totalTopicSet = totalKeywordList.stream()
                .map(Keyword::getTopic)
                .collect(Collectors.toSet());

        while(questionList.size() != questionCount){

            //2. 정답 키워드 선정
            List<KeywordSelectModel> answerKeywordSelectModelList
                    = makeAnswerKeywordSelectModelList(keywordRecordMap, totalKeywordList);
            Keyword answerKeyword = selectAnswerKeyword(answerKeywordSelectModelList);
            Topic answerTopic = answerKeyword.getTopic();
            Keyword answerKeyword2 = selectAnotherKeyword(answerTopic, answerKeyword, totalKeywordList);

            //3. 오답 주제 선정
            totalTopicSet.remove(answerTopic);
            List<Topic> wrongTopicList = getWrongTopic(new ArrayList<>(totalTopicSet), 3);

            QuestionDto questionDto = toQuestionDto(answerTopic, Arrays.asList(answerKeyword, answerKeyword2), wrongTopicList);
            questionList.add(questionDto);
        }
        return  questionList;
    }



    public List<QuestionDto> getJJHQuestion(List<Topic> topicList) {
        List<QuestionDto> questionList = new ArrayList<>();
        Map<QuestionCategory, List<Topic>> questionCategoryTopicMap = topicList.stream()
                .collect(Collectors.groupingBy(Topic::getQuestionCategory));

        //각 토픽에 대해서 문제 생성
        for (Topic answerTopic : topicList) {
            QuestionCategory answerQuestionCategory = answerTopic.getQuestionCategory();
            List<Keyword> answerTotalKeywordList = answerTopic.getKeywordList();
            List<Keyword> answerKeywordList = new ArrayList<>();
            //1. 2개의 정답 키워드를 랜덤하게 선정
            Set<Integer> randomIndex = getRandomIndex(2, answerTotalKeywordList.size());
            for (Integer index : randomIndex) {
                answerKeywordList.add(answerTotalKeywordList.get(index));
            }

            //2. 3개의 오답 주제 선정 -> questionCategory는 같은경우
            List<Topic> questionCategoryTopicList = questionCategoryTopicMap.get(answerQuestionCategory);
            List<Topic> wrongTotalTopicList = questionCategoryTopicList.stream()
                    .filter(t -> t != answerTopic)
                    .collect(Collectors.toList());
            List<Topic> wrongTopicList = getWrongTopic(wrongTotalTopicList, 3);

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
