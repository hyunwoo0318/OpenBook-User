package Project.OpenBook.Domain.Sentence.Service;

import Project.OpenBook.Domain.Sentence.Domain.Sentence;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Sentence.Dto.SentenceCreateDto;
import Project.OpenBook.Domain.Sentence.Dto.SentenceUpdateDto;
import Project.OpenBook.Domain.Sentence.Repository.SentenceRepository;
import Project.OpenBook.Handler.Exception.CustomException;
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
    public Sentence createSentence(SentenceCreateDto sentenceCreateDto) {
        Topic topic = topicRepository.findTopicByTitle(sentenceCreateDto.getTopic()).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });

        Sentence sentence = new Sentence(sentenceCreateDto.getName(), topic);
        sentenceRepository.save(sentence);
        return sentence;
    }

    @Transactional
    public Sentence updateSentence(Long sentenceId,SentenceUpdateDto sentenceUpdateDto) {
        Sentence sentence = checkSentence(sentenceId);

        sentence.updateSentence(sentenceUpdateDto.getName());
        return sentence;
    }


    @Transactional
    public boolean deleteSentence(Long sentenceId) {
        Sentence sentence = checkSentence(sentenceId);
        sentenceRepository.delete(sentence);
        return true;
    }

    private Sentence checkSentence(Long sentenceId) {
        return sentenceRepository.findById(sentenceId).orElseThrow(() ->{
            throw new CustomException(SENTENCE_NOT_FOUND);
        });
    }

}
