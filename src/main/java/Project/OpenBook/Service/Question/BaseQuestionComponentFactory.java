package Project.OpenBook.Service.Question;

import Project.OpenBook.Domain.Keyword;
import Project.OpenBook.Domain.Sentence;
import Project.OpenBook.Dto.question.QuestionChoiceDto;
import Project.OpenBook.Repository.keyword.KeywordRepository;
import Project.OpenBook.Repository.sentence.SentenceRepository;
import Project.OpenBook.Topic.Repo.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QSentence.sentence;

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
                .map(t -> QuestionChoiceDto.builder()
                        .choice(t.get(sentence.name))
                        .key(t.get(sentence.topic.title))
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
        return keywordRepository.queryKeywordsInTopic(topicTitle);
    }

    public List<Sentence> getTotalSentenceByAnswerTopic(String topicTitle) {
        return sentenceRepository.queryByTopicTitle(topicTitle);
    }

}
