package Project.OpenBook.Chapter.Service;

import Project.OpenBook.Chapter.Domain.Chapter;
import Project.OpenBook.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Chapter.Service.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterSimpleQueryService {

    private final ChapterRepository chapterRepository;
    private final ChapterValidator chapterValidator;

    public List<ChapterTitleNumDto> queryChaptersAdmin() {
        return chapterRepository.findAll().stream()
                .map(c -> new ChapterTitleNumDto(c.getTitle(), c.getNumber()))
                .collect(Collectors.toList());
    }

    public List<ChapterDetailDto> queryChaptersTotalInfo() {
        return chapterRepository.findAll().stream()
                .map(c -> {
                    return new ChapterDetailDto(c.getTitle(),
                            c.getNumber(),
                            c.getStartDate(),
                            c.getStartDate(),
                            c.getTopicList().size());
                })
                .collect(Collectors.toList());
    }

    public ChapterTitleDto queryChapterTitle(Integer num) {
        return new ChapterTitleDto(chapterValidator.checkChapter(num).getTitle());
    }

    public ChapterDateDto queryChapterDate(Integer num) {
        Chapter chapter = chapterValidator.checkChapter(num);
        return new ChapterDateDto(chapter.getStartDate(), chapter.getEndDate());
    }

    public ChapterInfoDto queryChapterInfo(Integer num) {
        return new ChapterInfoDto(chapterValidator.checkChapter(num).getContent());
    }

    public List<ChapterTopicWithCountDto> queryChapterTopicsAdmin(int num) {
        return chapterValidator.checkChapter(num).getTopicList().stream()
                .map(t -> new ChapterTopicWithCountDto(t))
                .collect(Collectors.toList());
    }


    public List<ChapterTopicUserDto> queryChapterTopicsCustomer(int num) {
        return chapterValidator.checkChapter(num).getTopicList().stream()
                .map(t -> new ChapterTopicUserDto(t))
                .collect(Collectors.toList());
    }
}
