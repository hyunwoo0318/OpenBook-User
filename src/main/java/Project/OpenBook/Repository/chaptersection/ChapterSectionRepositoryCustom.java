package Project.OpenBook.Repository.chaptersection;

import Project.OpenBook.Domain.ChapterSection;

import java.util.List;
import java.util.Optional;

public interface ChapterSectionRepositoryCustom {

    /**
     * 해당 단원의 모든 chapterSection을 쿼리하는 메서드
     * @param customerId 회원id
     * @param num 조회할 단원 번호
     * @return List<ChapterSection>
     */
    public List<ChapterSection> queryChapterSections(Long customerId, Integer num);
//
//    public List<ChapterSection> queryChapterSection(Integer num);
//
//    public List<ChapterSection> queryChapterSection(Long customerId);

    /**
     * 입력값에 모두 부합하는 chapterSection을 쿼리하는 메서드
     * @param customerId 회원id
     * @param chapterNum 단원번호
     * @param content content이름
     * @return Optional<ChapterSection>
     */
    public Optional<ChapterSection> queryChapterSection(Long customerId, Integer chapterNum, String content);

}
