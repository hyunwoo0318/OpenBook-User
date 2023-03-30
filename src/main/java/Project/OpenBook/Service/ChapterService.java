package Project.OpenBook.Service;

import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
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

    /**
     * chapter 추가 메서드
     * @param title
     * @param num
     * @return 성공적으로 완성된 Chapter -> 완성 실패시 null return
     */
    public Chapter creaeteChapter(String title, int number) {

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

    public Chapter updateChapter(int num, String inputTitle, int inputNum) {
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(num);
        if (chapterOptional.isEmpty()) {
            return null;
        }

        Chapter chapter = chapterOptional.get();

        Chapter updateChapter = chapter.updateChapter(inputTitle, inputNum);
        return updateChapter;
    }

    public Boolean deleteChapter(int num) {
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNumber(num);
        if (chapterOptional.isEmpty()) {
            return false;
        }

        //해당 단원의 상세정보의 단원정보를 null로 세팅
        List<Topic> topicList = topicRepository.findAllByChapter(chapterOptional.get());
        topicList.stream().forEach(t -> t.deleteChapter());

        //TODO : 학습분석을 구현하면 해당 학습분석을 처리하는 구문필요

        Chapter chapter = chapterOptional.get();
        chapterRepository.delete(chapter);
        return true;
    }
}
