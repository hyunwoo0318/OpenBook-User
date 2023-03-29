package Project.OpenBook.Service;

import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Repository.ChapterRepository;
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

    /**
     * chapter 추가 메서드
     * @param title
     * @param num
     * @return 성공적으로 완성된 Chapter -> 완성 실패시 null return
     */
    public Chapter addChapter(String title, int num) {

        //chapter num은 유니크한 칼럼이므로 겹치면 안됨.
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNum(num);
        if(chapterOptional.isPresent()){
            return null;
        }

        Chapter newChapter = Chapter.builder()
                .num(num)
                .title(title)
                .build();

        chapterRepository.save(newChapter);

        return newChapter;
    }

    public List<Chapter> getAllChapter() {
        return chapterRepository.findAll();
    }

    public Chapter updateChapter(int num, String inputTitle, int inputNum) {
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNum(num);
        if (chapterOptional.isEmpty()) {
            return null;
        }

        Chapter chapter = chapterOptional.get();

        Chapter updateChapter = chapter.updateChapter(inputTitle, inputNum);
        return updateChapter;
    }

    public Boolean deleteChapter(int num) {
        Optional<Chapter> chapterOptional = chapterRepository.findOneByNum(num);
        if (chapterOptional.isEmpty()) {
            return false;
        }

        Chapter chapter = chapterOptional.get();
        chapterRepository.delete(chapter);
        return true;
    }
}
