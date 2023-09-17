package Project.OpenBook.Domain.Question.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.Question.Dto.QuestionChoiceDto;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Keyword.Domain.Keyword;
import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;
import static Project.OpenBook.Domain.Keyword.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.Sentence.Domain.QSentence.sentence;


@Component
@RequiredArgsConstructor
public class BaseQuestionComponentFactory {

    private final TopicRepository topicRepository;
    private final KeywordRepository keywordRepository;
    private final SentenceRepository sentenceRepository;

    public List<Keyword> getKeywordsByAnswerTopic(String topicTitle, int limit) {
        return keywordRepository.queryKeywordsInTopicWithLimit(topicTitle, limit);
    }

    public List<Sentence> getSentencesByAnswerTopic(String topicTitle, int limit) {
        return sentenceRepository.queryByTopicTitle(topicTitle, limit);
    }

    public List<QuestionChoiceDto> getWrongKeywordsByTopic(String topicTitle, int limit) {
        return keywordRepository.queryWrongKeywords(topicTitle, limit).stream()
                .map(k -> QuestionChoiceDto.builder()
                        .choice(k.get(keyword.name))
                        .comment(k.get(keyword.comment))
                        .key(k.get(keyword.topic.title))
                        .build()
                ).collect(Collectors.toList());
    }

    public List<QuestionChoiceDto> getWrongSentencesByTopic(String topicTitle, int limit) {
        return sentenceRepository.queryWrongSentences(topicTitle, limit).stream()
                .map(s -> QuestionChoiceDto.builder()
                        .choice(s.getName())
                        .key(s.getTopic().getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    public List<QuestionChoiceDto> getWrongTopic(String answerTopicTitle, int limit) {
        return topicRepository.queryWrongTopicTitle(answerTopicTitle,limit).stream()
                .map(t -> QuestionChoiceDto.builder()
                        .key(t)
                        .choice(t)
                        .build())
                .collect(Collectors.toList());
    }

    public List<QuestionChoiceDto> toQuestionChoiceDtoByKeyword(List<Keyword> keywordList) {
        return keywordList.stream()
                .map(k -> QuestionChoiceDto.builder()
                        .choice(k.getName())
                        .comment(k.getComment())
                        .key(k.getTopic().getTitle())
                        .build()
                ).collect(Collectors.toList());
    }

    public List<QuestionChoiceDto> toQuestionChoiceDtoBySentence(List<Sentence> sentenceList) {
        return sentenceList.stream()
                .map(s -> QuestionChoiceDto.builder()
                        .choice(s.getName())
                        .key(s.getTopic().getTitle())
                        .build())
                .collect(Collectors.toList());
    }

    public List<Keyword> getTotalKeywordByAnswerTopic(String topicTitle) {
        Topic topic = checkTopic(topicTitle);
        return topic.getKeywordList();
    }

    public List<Sentence> getTotalSentenceByAnswerTopic(String topicTitle) {
        Topic topic = checkTopic(topicTitle);
        return topic.getSentenceList();
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

}
