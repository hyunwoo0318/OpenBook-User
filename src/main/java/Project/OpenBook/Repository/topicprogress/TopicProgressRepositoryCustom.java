package Project.OpenBook.Repository.topicprogress;

import Project.OpenBook.Domain.TopicProgress;

import java.util.List;
import java.util.Optional;

public interface TopicProgressRepositoryCustom {

    /**
     * 특정 회원의 특정 주제에 대한 topicProgress를 쿼리하는 메서드
     * @param customerId 회원id
     * @param topicTitle 주제 제목
     * @return Optional<TopicProgress>
     */
    public Optional<TopicProgress> queryTopicProgress(Long customerId, String topicTitle );

    /**
     * 특정 회원의 특정 단원 내의 topicProgressList를 쿼리하는 메서드
     * @param customerId 회원id
     * @param chapterNum 단원번호
     * @return List<TopicProgress>
     */
    public List<TopicProgress> queryTopicProgresses(Long customerId, Integer chapterNum);
}
