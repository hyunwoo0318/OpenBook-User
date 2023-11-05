package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Constants.ChoiceType;
import Project.OpenBook.Constants.QuestionConst;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Keyword.Dto.KeywordIdNameDto;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Question.Dto.QuizChoiceDto;
import Project.OpenBook.Domain.QuestionCategory.Domain.QuestionCategory;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.WeightedRandomSelection.Model.AnswerKeywordSelectModel;
import Project.OpenBook.WeightedRandomSelection.Model.TopicSelectModel;
import Project.OpenBook.WeightedRandomSelection.WeightedRandomService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.QuestionConst.GET_TOPIC_BY_KEYWORD_TYPE;

public class GetTopicByKeywordQuestion extends BaseQuestionComponentFactory implements QuestionFactory {
    public GetTopicByKeywordQuestion(TopicRepository topicRepository, KeywordRepository keywordRepository,
                                     WeightedRandomService weightedRandomService) {
        super(topicRepository, keywordRepository, weightedRandomService);
    }

    @Override
    public List<QuestionDto> getQuestion(Map<Topic, TopicLearningRecord> topicRecordMap, Map<Keyword, KeywordLearningRecord> keywordRecordMap,
                                         QuestionCategory questionCategory, Integer questionCount){
        List<QuestionDto> questionList = new ArrayList<>();

        List<TopicSelectModel> topicSelectModelList = makeTopicSelectModelList(topicRecordMap, questionCategory);
        for (int i = 0; i < questionCount; i++) {
            //1. 정답 주제 선정
            Topic answerTopic = selectAnswerTopic(topicSelectModelList);

            //2. 정답 키워드 선정
            List<AnswerKeywordSelectModel> answerKeywordSelectModelList
                    = makeAnswerKeywordSelectModelList(keywordRecordMap, answerTopic);
            List<Keyword> answerKeywordList = selectAnswerKeywordList(answerKeywordSelectModelList, 2);

            //3. 오답 주제 선정
            List<TopicSelectModel> wrongTopicSelectModelList = topicSelectModelList.stream()
                    .filter(topicSelectModel -> !topicSelectModel.getTopic().getTitle().equals(answerTopic.getTitle()))
                    .collect(Collectors.toList());
            List<Topic> wrongTopicList = selectWrongTopicList(wrongTopicSelectModelList, 3);

            QuestionDto questionDto = toQuestionDto(answerTopic, answerKeywordList, wrongTopicList);
            questionList.add(questionDto);
        }
        return  questionList;
    }

    public QuestionDto getJJHQuestion(String topicTitle) {
        List<KeywordIdNameDto> descriptionWithIdList = getKeywordsByAnswerTopic(topicTitle, 2).stream()
                .map(k -> new KeywordIdNameDto(k.getName(), k.getId()))
                .collect(Collectors.toList());

        if (descriptionWithIdList.isEmpty()) {
            return null;
        }

        //오답 주제 조회
        List<QuizChoiceDto> choiceList = getWrongTopic(topicTitle, QuestionConst.GET_TOPIC_WRONG_ANSWER_NUM);

        //정답 선지 추가
        choiceList.add(new QuizChoiceDto(topicTitle, topicTitle));

        //Dto 변환
        List<String> descriptionList = new ArrayList<>();
        List<Long> keywordIdList = new ArrayList<>();
        for (KeywordIdNameDto dto : descriptionWithIdList) {
            descriptionList.add(dto.getName());
            keywordIdList.add(dto.getId());
        }
        return toQuestionDto(topicTitle, descriptionList, keywordIdList, choiceList);
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
