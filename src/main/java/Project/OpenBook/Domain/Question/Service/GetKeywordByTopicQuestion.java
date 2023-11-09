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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<QuestionDto> getQuestion(Map<Keyword, KeywordLearningRecord> keywordRecordMap,
                                         List<Keyword> totalKeywordList, Integer questionCount) {
        List<QuestionDto> questionList = new ArrayList<>();

        while(questionList.size() != questionCount) {
            //1. 정답 키워드 선정
            List<KeywordSelectModel> answerKeywordSelectModelList
                    = makeAnswerKeywordSelectModelList(keywordRecordMap, totalKeywordList);
            Keyword answerKeyword = selectAnswerKeyword(answerKeywordSelectModelList);
            if(answerKeyword == null) continue;
            Topic answerTopic = answerKeyword.getTopic();

            //2. 오답 키워드 선정
            List<Keyword> totalWrongKeywordList = totalKeywordList.stream()
                    .filter(k -> k.getTopic() != answerTopic)
                    .collect(Collectors.toList());

            //questionProb의 확률에 기반해서 3개의 오답 키워드 선정
            List<Keyword> wrongKeywordList = getWrongKeywords(totalWrongKeywordList, WRONG_KEYWORD_NUM);

            //Dto 변환
            if(!wrongKeywordList.isEmpty()){
                QuestionDto question = toQuestionDto(answerTopic.getTitle(), Arrays.asList(answerKeyword), wrongKeywordList);
                questionList.add(question);
            }
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

    public List<QuestionDto> getJJHQuestion(String topicTitle, int questionCount) {
        List<QuestionDto> questionList = new ArrayList<>();

        //해당 토픽의 전체 키워드 조회
        List<Keyword> answerKeywordList = getTotalKeywordByAnswerTopic(topicTitle);
        List<String> answerKeywordNameList = answerKeywordList.stream()
                .map(Keyword::getName)
                .collect(Collectors.toList());

        //오답 키워드가 가능한 모든 키워드 조회
        List<Keyword> totalWrongKeywordList = getTotalWrongKeywords(answerKeywordNameList,topicTitle);

        //해당 토픽의 전체 키워드에 대해서 각각 문제 생성
        for (Keyword answerKeyword : answerKeywordList) {
            //questionProb의 확률에 기반해서 3개의 오답 키워드 선정
            List<Keyword> wrongKeywordList = getWrongKeywords(totalWrongKeywordList, WRONG_KEYWORD_NUM);

            //Dto 변환
            if(!wrongKeywordList.isEmpty()){
                QuestionDto question = toQuestionDto(topicTitle, Arrays.asList(answerKeyword), wrongKeywordList);
                questionList.add(question);
            }
        }


        //questionCount보다 모든 키워드 개수가 적은 경우 앞 컨텐츠에서 랜덤하게 keyword에서 골라서 생성
        Integer leftQuestionCount = questionCount - answerKeywordList.size();
        if (leftQuestionCount > 0) {


        }
        return questionList;
    }

//    private List<QuestionDto> getAdditionalQuestions(String answerTopicTitle, int count) {
//        getTotalAnswerKeywordsInJJH(answerTopicTitle);
//        //1. 앞 컨텐츠에서 (jjhContentNumber가 작은 topic에 속해있는 keyword중 랜덤하게 선택)
//
//
//        //2. 정답 키워드와 같은 q.c내부에 있는 키워드, (topic, name)이 다른 오답 키워드중
//        //questionProb에 비례해서 문제 생성
//    }

    private QuestionDto toQuestionDto(String topicTitle, List<Keyword> answerKeywordList, List<Keyword> wrongKeywordList) {
        List<QuizChoiceDto> choiceList = new ArrayList<>();
        List<Long> keywordList = new ArrayList<>();

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



}
