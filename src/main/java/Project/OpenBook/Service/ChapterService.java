package Project.OpenBook.Service;

import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.ErrorDto;
import Project.OpenBook.Repository.ChapterRepository;
import Project.OpenBook.Repository.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;


    public Chapter createChapter(String title, int number) {

        //chapter num은 유니크한 칼럼이므로 겹치면 안됨.
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(number);
        if(chapterOptional.isPresent()){
            return null;
        }

        Chapter newChapter = Chapter.builder()
                .number(number)
                .title(title)
                .build();

        chapterRepository.save(newChapter);

        return newChapter;
    }

    public List<Chapter> getAllChapter() {
        return chapterRepository.findAll();
    }

    public List<Topic> getTopicsInChapter(int number) {
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(number);
        if (chapterOptional.isEmpty()) {
            return null;
        }
        Chapter chapter = chapterOptional.get();
        return topicRepository.findAllByChapter(chapter);
    }

    public Chapter updateChapter(int num, String inputTitle, int inputNum) {
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(num);
        if (chapterOptional.isEmpty()) {
            return null;
        }

        Chapter chapter = chapterOptional.get();

        Chapter updateChapter = chapter.updateChapter(inputTitle, inputNum);
        return updateChapter;
    }

    public Boolean deleteChapter(int num, List<ErrorDto> errorDtoList) {
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(num);
        if (chapterOptional.isEmpty()) {
            return false;
        }

        //해당 단원의 상세정보의 단원정보를 null로 세팅
        List<Topic> topicList = topicRepository.findAllByChapter(chapterOptional.get());

        if(!topicList.isEmpty()){
            errorDtoList.add(new ErrorDto("topic", "해당 단원에 토픽이 존재합니다."));
            return false;
        }

        topicList.stream().forEach(t -> t.deleteChapter());

        //TODO : 학습분석을 구현하면 해당 학습분석을 처리하는 구문필요

        Chapter chapter = chapterOptional.get();
        chapterRepository.delete(chapter);
        return true;
    }
}
