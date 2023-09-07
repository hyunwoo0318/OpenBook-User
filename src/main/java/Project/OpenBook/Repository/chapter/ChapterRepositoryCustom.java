package Project.OpenBook.Repository.chapter;

import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.ChapterProgress;


import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChapterRepositoryCustom {

    /**
     * 단원 전체 정보를 사용자 progress와 같이 쿼리하는 메서드
     * @param customerId 사용자 id
     * @return <chapter, chapterProgress>이 매핑된 Map 리턴
     * 특정 chapter에 chapterProgress가 존재하지 않을 경우 value값에 null 리턴
     */
    public Map<Chapter, ChapterProgress> queryChapterWithProgress(Long customerId);

    public Integer queryMaxChapterNum();

    public Optional<Chapter> queryChapterWithTopic(int num);

    public List<Chapter> queryChapterListWithTopic();
}
