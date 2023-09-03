package Project.OpenBook.Service;

import Project.OpenBook.Constants.QuestionConst;
import Project.OpenBook.Dto.keyword.KeywordNameCommentDto;
import Project.OpenBook.Dto.question.*;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.sentence.SentenceRepository;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Constants.QuestionConst.*;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QSentence.sentence;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final TopicRepository topicRepository;
    private final ChapterRepository chapterRepository;
    private final KeywordRepository keywordRepository;
    private final SentenceRepository sentenceRepository;

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    private Chapter checkChapter(Integer num) {
        return chapterRepository.findOneByNumber(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });
    }


    public List<TimeFlowQuestionDto> queryTimeFlowQuestion(Integer num) {
        Chapter chapter = checkChapter(num);

        Map<Topic, List<PrimaryDate>> m = topicRepository.queryTimeFlowQuestion(num);

        List<TimeFlowQuestionDto> timeFlowQuestionDtoList = new ArrayList<>();
        for (Topic topic  : m.keySet()) {
            String topicTitle = topic.getTitle();
            if (topic.getStartDateCheck()) {
                timeFlowQuestionDtoList.add(new TimeFlowQuestionDto(topic.getStartDate(), makeComment(topicTitle, "startDate"),topicTitle));
            }
            if (topic.getEndDateCheck()) {
                timeFlowQuestionDtoList.add(new TimeFlowQuestionDto(topic.getEndDate(), makeComment(topicTitle, "endDate"), topicTitle));
            }
            List<PrimaryDate> primaryDateList = m.get(topic);
            for (PrimaryDate primaryDate : primaryDateList) {
                if (primaryDate.getExtraDateCheck()) {
                    timeFlowQuestionDtoList.add(new TimeFlowQuestionDto(primaryDate.getExtraDate(), primaryDate.getExtraDateComment(), topicTitle));
                }
            }
        }

        //연도 순으로 오름차순으로 정렬
        Collections.sort(timeFlowQuestionDtoList, Comparator.comparing(TimeFlowQuestionDto::getDate));

        return timeFlowQuestionDtoList;
    }

    public String makeComment(String topicTitle, String type) {
        if(type.equals("startDate")){
            return topicTitle + "의 시작연도입니다.";
        } else {
            return topicTitle + "의 종료연도입니다.";
        }
    }

    @Transactional
    public List<QuestionDto> queryGetKeywordsQuestion(String topicTitle) {
        Topic topic = checkTopic(topicTitle);
        List<QuestionDto> questionDtoList = new ArrayList<>();

        //정답 키워드조회
        List<QuestionChoiceDto> answerChoiceList = keywordRepository.queryKeywordsInTopic(topicTitle)
                .stream()
                .map(k -> QuestionChoiceDto.builder()
                        .choice(k.getName())
                        .comment(k.getComment())
                        .key(topicTitle)
                        .build()
                ).collect(Collectors.toList());

        int answerKeywordSize = answerChoiceList.size();

        //오답 키워드조회
        List<QuestionChoiceDto> wrongAnswerChoiceList = keywordRepository.queryWrongKeywords(topicTitle, answerKeywordSize * WRONG_KEYWORD_SENTENCE_NUM).stream()
                .map(k -> QuestionChoiceDto.builder()
                        .choice(k.get(keyword.name))
                        .comment(k.get(keyword.comment))
                        .key(k.get(keyword.topic.title))
                        .build()
                ).collect(Collectors.toList());
        int wrongKeywordSize = wrongAnswerChoiceList.size();

        if(wrongKeywordSize < WRONG_KEYWORD_SENTENCE_NUM){
            throw new CustomException(QUESTION_ERROR);
        }

        //return할 dto생성
        /**
         * 충분한 keyword가 존재하는 경우 -> 각 문제마다 다른 오답 선지 제공
         */
        if(wrongKeywordSize == answerKeywordSize * WRONG_KEYWORD_SENTENCE_NUM)  {
            int idx = 0;
            for (QuestionChoiceDto questionChoiceDto : answerChoiceList) {
                List<QuestionChoiceDto> choiceList = new ArrayList<>();
                choiceList.add(questionChoiceDto);
                List<QuestionChoiceDto> wrongChoiceList = wrongAnswerChoiceList.subList(idx, idx + WRONG_KEYWORD_SENTENCE_NUM);
                idx += WRONG_KEYWORD_SENTENCE_NUM;
                choiceList.addAll(wrongChoiceList);

                QuestionDto dto = QuestionDto.builder()
                        .questionType(GET_KEYWORD_TYPE)
                        .answer(topicTitle)
                        .choiceList(choiceList)
                        .build();
                questionDtoList.add(dto);
            }
        }
        /**
         * 충분한 keyword가 존재하지 않는 경우 -> 존재하는 키워드중 랜덤하게 WRONG_ANSWER_NUM만큼 골라서 오답 선지로 제공
         */
        else{
            for (QuestionChoiceDto questionChoiceDto : answerChoiceList) {
                List<QuestionChoiceDto> choiceList = new ArrayList<>();
                choiceList.add(questionChoiceDto);
                Collections.shuffle(wrongAnswerChoiceList);
                List<QuestionChoiceDto> wrongChoiceList = wrongAnswerChoiceList.subList(0, WRONG_KEYWORD_SENTENCE_NUM);

                choiceList.addAll(wrongChoiceList);

                QuestionDto dto = QuestionDto.builder()
                        .questionType(GET_KEYWORD_TYPE)
                        .answer(topicTitle)
                        .choiceList(choiceList)
                        .build();
                questionDtoList.add(dto);
            }
        }


        return questionDtoList;
    }

    @Transactional
    public List<QuestionDto> queryGetSentencesQuestion(String topicTitle) {
        Topic topic = checkTopic(topicTitle);
        List<QuestionDto> questionDtoList = new ArrayList<>();

        //정답 문장 조회
        List<QuestionChoiceDto> answerChoiceList = sentenceRepository.queryByTopicTitle(topicTitle).stream()
                .map(s -> QuestionChoiceDto.builder()
                        .choice(s.getName())
                        .key(topicTitle)
                        .build())
                .collect(Collectors.toList());
        int answerSentenceSize = answerChoiceList.size();


        //오답 문장 조회
        List<QuestionChoiceDto> wrongAnswerChoiceList = sentenceRepository.queryWrongSentences(topicTitle, answerSentenceSize * WRONG_KEYWORD_SENTENCE_NUM).stream()
                .map(t -> QuestionChoiceDto.builder()
                        .choice(t.get(sentence.name))
                        .key(t.get(sentence.topic.title))
                        .build())
                .collect(Collectors.toList());

        int wrongSentenceSize = wrongAnswerChoiceList.size();

        if(wrongSentenceSize < WRONG_KEYWORD_SENTENCE_NUM){
            throw new CustomException(QUESTION_ERROR);
        }

        //return할 dto생성
        /**
         * 충분한 sentence가 존재하는 경우 -> 각 문제마다 다른 오답 선지 제공
         */
        if(wrongSentenceSize == answerSentenceSize * WRONG_KEYWORD_SENTENCE_NUM)  {
            int idx = 0;
            for (QuestionChoiceDto questionChoiceDto : answerChoiceList) {
                List<QuestionChoiceDto> choiceList = new ArrayList<>();
                choiceList.add(questionChoiceDto);
                List<QuestionChoiceDto> wrongChoiceList = wrongAnswerChoiceList.subList(idx, idx + WRONG_KEYWORD_SENTENCE_NUM);
                idx += WRONG_KEYWORD_SENTENCE_NUM;
                choiceList.addAll(wrongChoiceList);

                QuestionDto dto = QuestionDto.builder()
                        .questionType(GET_SENTENCE_TYPE)
                        .answer(topicTitle)
                        .choiceList(choiceList)
                        .build();
                questionDtoList.add(dto);
            }
        }
        /**
         * 충분한 sentence가 존재하지 않는 경우 -> 존재하는 sentence중 랜덤하게 WRONG_ANSWER_NUM만큼 골라서 오답 선지로 제공
         */
        else{
            for (QuestionChoiceDto questionChoiceDto : answerChoiceList) {
                List<QuestionChoiceDto> choiceList = new ArrayList<>();
                choiceList.add(questionChoiceDto);
                Collections.shuffle(wrongAnswerChoiceList);
                List<QuestionChoiceDto> wrongChoiceList = wrongAnswerChoiceList.subList(0, WRONG_KEYWORD_SENTENCE_NUM);

                choiceList.addAll(wrongChoiceList);

                QuestionDto dto = QuestionDto.builder()
                        .questionType(GET_SENTENCE_TYPE)
                        .answer(topicTitle)
                        .choiceList(choiceList)
                        .build();
                questionDtoList.add(dto);
            }
        }

        return questionDtoList;
    }

    public List<QuestionDto> queryGetTopicsByKeywordQuestion(Integer num) {
        checkChapter(num);

        List<QuestionDto> questionDtoList = new ArrayList<>();

        List<String> topicTitleList = topicRepository.queryTopicTitleInChapter(num);
        for (String topicTitle : topicTitleList) {

            List<Keyword> answerKeywordList = keywordRepository.queryKeywordsInTopicWithLimit(topicTitle, 2);

            if (answerKeywordList.size() == 2) {
                List<KeywordNameCommentDto> answerKeywordDtoList = answerKeywordList.stream().map(k -> new KeywordNameCommentDto(k.getName(), k.getComment()))
                        .collect(Collectors.toList());
                List<String> topicTitles = topicRepository.queryWrongTopicTitle(topicTitle, GET_TOPIC_WRONG_ANSWER_NUM);
                topicTitles.add(topicTitle);
                List<QuestionChoiceDto> choiceList = topicTitles.stream()
                        .map(t -> QuestionChoiceDto.builder()
                                .key(t)
                                .choice(t)
                                .build())
                        .collect(Collectors.toList());

                QuestionDto dto = QuestionDto.builder()
                        .questionType(GET_TOPIC_BY_KEYWORD_TYPE)
                        .answer(topicTitle)
                        .choiceList(choiceList)
                        .descriptionKeyword(answerKeywordDtoList)
                        .build();
                questionDtoList.add(dto);
            }
        }

        return questionDtoList;
    }

    public List<QuestionDto> queryGetTopicsBySentenceQuestion(Integer num) {
        checkChapter(num);

        List<QuestionDto> questionDtoList = new ArrayList<>();

        List<String> topicTitleList = topicRepository.queryTopicTitleInChapter(num);
        for (String topicTitle : topicTitleList) {

            List<Sentence> answerSentenceList = sentenceRepository.queryByTopicTitle(topicTitle, 1);

            if (answerSentenceList.size() == 1) {
                String answerSentence = answerSentenceList.get(0).getName();
                List<String> wrongTopicList = topicRepository.queryWrongTopicTitle(topicTitle, GET_TOPIC_WRONG_ANSWER_NUM);
                wrongTopicList.add(topicTitle);
                List<QuestionChoiceDto> choiceList = wrongTopicList.stream()
                        .map(t -> QuestionChoiceDto.builder()
                                .choice(t)
                                .key(t)
                                .build())
                        .collect(Collectors.toList());

                QuestionDto dto = QuestionDto.builder()
                        .questionType(GET_TOPIC_BY_SENTENCE_TYPE)
                        .answer(topicTitle)
                        .choiceList(choiceList)
                        .descriptionSentence(answerSentence)
                        .build();

                questionDtoList.add(dto);
            }
        }

        return questionDtoList;
    }
}
