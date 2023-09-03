package Project.OpenBook.Repository.chapterProgress;

import Project.OpenBook.Domain.ChapterProgress;

import java.util.List;
import java.util.Optional;

public interface ChapterProgressRepositoryCustom {

    /**
     * 특정 회원의 특정 단원에 대한 chapterProgress를 쿼리하는 메서드
     * @param customerId 특정 회원id
     * @param chapterNum 특정 단원 번호
     * @return Optional<ChapterProgress>
     */
    public Optional<ChapterProgress> queryChapterProgress(Long customerId, Integer chapterNum);
//
//    public List<ChapterProgress> queryChapterProgress(Integer chapterNum);
}
