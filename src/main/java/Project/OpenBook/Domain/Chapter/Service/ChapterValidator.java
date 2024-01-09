package Project.OpenBook.Domain.Chapter.Service;

import static Project.OpenBook.Constants.ErrorCode.CHAPTER_NOT_FOUND;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChapterValidator {

  private final ChapterRepository chapterRepository;

  public Chapter checkChapter(int num) {
    return chapterRepository
        .findOneByNumber(num)
        .orElseThrow(() -> new CustomException(CHAPTER_NOT_FOUND));
  }
}
