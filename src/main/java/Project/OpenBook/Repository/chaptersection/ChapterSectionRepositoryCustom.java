package Project.OpenBook.Repository.chaptersection;

import Project.OpenBook.Domain.ChapterSection;

import java.util.List;
import java.util.Optional;

public interface ChapterSectionRepositoryCustom {

    public List<ChapterSection> queryChapterSection(Long customerId, Integer num);

    public List<ChapterSection> queryChapterSection(Integer num);

    public List<ChapterSection> queryChapterSection(Long customerId);

    public Optional<ChapterSection> queryChapterSection(Long customerId, Integer chapterNum, String content);

}
