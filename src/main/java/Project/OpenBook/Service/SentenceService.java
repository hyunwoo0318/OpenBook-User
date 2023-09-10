package Project.OpenBook.Service;

import Project.OpenBook.Domain.Sentence;
import Project.OpenBook.Topic.Domain.Topic;
import Project.OpenBook.Dto.sentence.SentenceCreateDto;
import Project.OpenBook.Dto.sentence.SentenceUpdateDto;
import Project.OpenBook.Repository.sentence.SentenceRepository;
import Project.OpenBook.Topic.Repo.TopicRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static Project.OpenBook.Constants.ErrorCode.SENTENCE_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.TOPIC_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class SentenceService {

    private final SentenceRepository sentenceRepository;

    private final TopicRepository topicRepository;

    @Transactional
    public void createSentence(SentenceCreateDto sentenceCreateDto) {
        Topic topic = topicRepository.findTopicByTitle(sentenceCreateDto.getTopic()).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });

        Sentence sentence = new Sentence(sentenceCreateDto.getName(), topic);
        sentenceRepository.save(sentence);
    }

    @Transactional
    public void updateSentence(Long sentenceId,SentenceUpdateDto sentenceUpdateDto) {
        Sentence sentence = checkSentence(sentenceId);

        sentence.updateSentence(sentenceUpdateDto.getName());
    }

    private Sentence checkSentence(Long sentenceId) {
        return sentenceRepository.findById(sentenceId).orElseThrow(() ->{
            throw new CustomException(SENTENCE_NOT_FOUND);
        });
    }

    public void deleteSentence(Long sentenceId) {
        Sentence sentence = checkSentence(sentenceId);
        sentenceRepository.delete(sentence);

    }
}
