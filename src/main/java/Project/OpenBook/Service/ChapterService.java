package Project.OpenBook.Service;

import Project.OpenBook.Dto.topic.AdminChapterDto;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@Transactional
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;


    public Chapter createChapter(String title, int number) {
        checkChapterNum(number);

        Chapter newChapter = Chapter.builder()
                .number(number)
                .title(title)
                .build();

        chapterRepository.save(newChapter);

        return newChapter;
    }

    public List<Chapter> queryAllChapters() {
        return chapterRepository.findAll();
    }

    public List<AdminChapterDto> queryTopicsInChapter(int number) {
        checkChapter(number);
        return topicRepository.queryAdminChapterDto(number);
    }

    public Chapter updateChapter(int num, String inputTitle, int inputNum) {
        if(num != inputNum){
            checkChapterNum(inputNum);
        }
        Chapter chapter = checkChapter(num);

        Chapter updateChapter = chapter.updateChapter(inputTitle, inputNum);
        return updateChapter;
    }

    public Boolean deleteChapter(int num) {
        Chapter chapter = checkChapter(num);

        //해당 단원에 토픽이 존재하는 경우 단원 삭제 불가능
        List<Topic> topicList = topicRepository.findAllByChapter(chapter);
        if(!topicList.isEmpty()){
            throw new CustomException(CHAPTER_HAS_TOPIC);
        }

        //TODO : 학습분석을 구현하면 해당 학습분석을 처리하는 구문필요

        chapterRepository.delete(chapter);
        return true;
    }
    public String queryChapterTitle(Integer num) {
        Chapter chapter = checkChapter(num);
        return chapter.getTitle();
    }

    public String queryChapterInfo(Integer num) {
        Chapter chapter = checkChapter(num);
        return chapter.getContent();
    }

    private void checkChapterNum(int number) {
        chapterRepository.findOneByNumber(number).ifPresent(c -> {
            throw new CustomException(DUP_CHAPTER_NUM);
        });
    }

    private Chapter checkChapter(int num) {
        return chapterRepository.findOneByNumber(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });
    }

    public void updateChapterInfo(Integer num, String content) {
        Chapter chapter = checkChapter(num);

        chapter.updateContent(content);
    }
}
