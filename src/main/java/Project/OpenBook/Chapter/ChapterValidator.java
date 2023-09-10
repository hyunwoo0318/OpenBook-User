package Project.OpenBook.Chapter;

import Project.OpenBook.Chapter.Domain.Chapter;
import Project.OpenBook.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static Project.OpenBook.Constants.ErrorCode.CHAPTER_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.DUP_CHAPTER_NUM;

@Component
@RequiredArgsConstructor
public class ChapterValidator {

    private final ChapterRepository chapterRepository;


    public void checkDupChapterNum(int number) {
        chapterRepository.findOneByNumber(number).ifPresent(c -> {
            throw new CustomException(DUP_CHAPTER_NUM);
        });
    }

    public Chapter checkChapter(int num) {
        return chapterRepository.findOneByNumber(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });
    }

}
