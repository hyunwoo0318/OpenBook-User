package Project.OpenBook.Domain.Chapter.Repo;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.StudyProgress.ChapterProgress.Domain.ChapterProgress;


import java.util.Map;

public interface ChapterRepositoryCustom {

    /**
     * 단원 전체 정보를 사용자 progress와 같이 쿼리하는 메서드
     * @param customerId 사용자 id
     * @return <chapter, chapterProgress>이 매핑된 Map 리턴
     * 특정 chapter에 chapterProgress가 존재하지 않을 경우 value값에 null 리턴
     */
    public Map<Chapter, ChapterProgress> queryChapterWithProgress(Long customerId);

    public Integer queryMaxChapterNum();
}
