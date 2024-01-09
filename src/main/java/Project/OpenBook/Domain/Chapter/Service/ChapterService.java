package Project.OpenBook.Domain.Chapter.Service;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterDateDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterDetailDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterInfoDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterTitleDto;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterService {

  private final ChapterRepository chapterRepository;
  private final ChapterValidator chapterValidator;

  public List<ChapterDetailDto> queryChaptersTotalInfo() {
    return chapterRepository.findAll().stream()
        .map(
            c -> {
              return new ChapterDetailDto(
                  c.getTitle(), c.getNumber(), c.getDateComment(), c.getTopicList().size());
            })
        .sorted(Comparator.comparing(ChapterDetailDto::getNumber))
        .collect(Collectors.toList());
  }

  public ChapterTitleDto queryChapterTitle(Integer num) {
    return new ChapterTitleDto(chapterValidator.checkChapter(num).getTitle());
  }

  public ChapterDateDto queryChapterDate(Integer num) {
    Chapter chapter = chapterValidator.checkChapter(num);
    return new ChapterDateDto(chapter.getDateComment());
  }

  public ChapterInfoDto queryChapterInfo(Integer num) {
    return new ChapterInfoDto(chapterValidator.checkChapter(num).getContent());
  }
}
