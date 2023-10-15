package Project.OpenBook.Domain.Chapter.Service;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Chapter.Service.dto.*;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChapterSimpleQueryService {

    private final ChapterRepository chapterRepository;
    private final ChapterValidator chapterValidator;

    public List<ChapterQueryAdminDto> queryChaptersAdmin() {
        return chapterRepository.findAll().stream()
                .map(c -> new ChapterQueryAdminDto(c.getTitle(), c.getNumber(), c.getDateComment()))
                .sorted(Comparator.comparing(ChapterQueryAdminDto::getNumber))
                .collect(Collectors.toList());
    }

    public List<ChapterDetailDto> queryChaptersTotalInfo() {
        return chapterRepository.findAll().stream()
                .map(c -> {
                    return new ChapterDetailDto(c.getTitle(),
                            c.getNumber(),
                            c.getDateComment(),
                            c.getTopicList().size());
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

    public List<ChapterTopicWithCountDto> queryChapterTopicsAdmin(int num) {
        List<Topic> topicList = chapterValidator.checkChapter(num).getTopicList();
        return topicList.stream()
                .map(t -> new ChapterTopicWithCountDto(t))
                .sorted(Comparator.comparing(ChapterTopicWithCountDto::getNumber))
                .collect(Collectors.toList());
    }


    public List<ChapterTopicUserDto> queryChapterTopicsCustomer(int num) {
        return chapterValidator.checkChapter(num).getTopicList().stream()
                .sorted(Comparator.comparing(Topic::getNumber))
                .map(t -> new ChapterTopicUserDto(t))
                .collect(Collectors.toList());
    }
}
