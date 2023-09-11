package Project.OpenBook.Chapter.Service;

import Project.OpenBook.Chapter.ChapterValidator;
import Project.OpenBook.Chapter.Controller.dto.ChapterUserDto;
import Project.OpenBook.Chapter.Domain.Chapter;
import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.studyProgress.ProgressDto;
import Project.OpenBook.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Topic.Domain.Topic;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChapterWithProgressService {

    private final ChapterRepository chapterRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final ChapterSectionRepository chapterSectionRepository;
    private final TopicProgressRepository topicProgressRepository;
    private final ChapterValidator chapterValidator;

    /**
     * 단원 전체 정보와 회원별 progress,state를 리턴하는 메서드
     * @param customer 회원정보
     * @return {chapter.title, chapter.number, state, progress}
     * 1단원의 경우 default => state = Open, progress = ChapterInfo
     * 2단원 이후의 경우 default => state = Locked, progress = NotStarted
     */
    @Transactional
    public List<ChapterUserDto> queryChapterUserDtos(Customer customer) {
        Long customerId = customer.getId();

        List<ChapterUserDto> chapterUserDtoList = new ArrayList<>();
        Map<Chapter, ChapterProgress> map = chapterRepository.queryChapterWithProgress(customerId);
        for (Chapter chapter : map.keySet()) {
            ChapterProgress findChapterProgress = map.get(chapter);

            if (findChapterProgress == null) {
                if(chapter.getNumber() == 1)
                {
                    findChapterProgress = new ChapterProgress(customer, chapter, 0, ContentConst.CHAPTER_INFO.getName());
                }else{
                    findChapterProgress = new ChapterProgress(customer, chapter, 0, ContentConst.NOT_STARTED.getName());
                }
                chapterProgressRepository.save(findChapterProgress);
            }
            String state = StateConst.LOCKED.getName();
            if(!findChapterProgress.getProgress().equals(ContentConst.NOT_STARTED.getName())){
                state = StateConst.OPEN.getName();
            }
            ChapterUserDto chapterUserDto = new ChapterUserDto(chapter.getTitle(), chapter.getNumber(), state, findChapterProgress.getProgress());
            chapterUserDtoList.add(chapterUserDto);
        }

        List<ChapterUserDto> sortedChapterUserDtoList = chapterUserDtoList.stream()
                .sorted(Comparator.comparing(ChapterUserDto::getNumber))
                .collect(Collectors.toList());


        return sortedChapterUserDtoList;
    }


    /**
     * 목차 제공하는 메서드
     * @param customer 회원정보
     * @param chapterNum 단원번호
     * @return {content, title(단원이면 단원제목, 주제면 주제제목), state}
     * 특정 chapterSection이 존재하지 않는 경우 생성해서 제공
     * 존재하지 않는 chapterNum 입력 -> CHAPTER_NOT_FOUND Exception
     */
    @Transactional
    public List<ProgressDto> queryContentTable(Customer customer, Integer chapterNum) {
        Chapter chapter = chapterValidator.checkChapter(chapterNum);
        Long customerId = customer.getId();
        String title = chapter.getTitle();
        HashMap<String, ChapterSection> chapterMap = new HashMap<>();
        HashMap<String, TopicProgress> topicMap = new HashMap<>();
        List<ProgressDto> contentTableList = new ArrayList<>();
        List<ChapterSection> chapterSectionList = chapterSectionRepository.queryChapterSections(customerId, chapterNum);
        for (ChapterSection chapterSection : chapterSectionList) {
            chapterMap.put(chapterSection.getContent(), chapterSection);
        }
        List<TopicProgress> topicProgressList = topicProgressRepository.queryTopicProgresses(customerId, chapterNum);
        for (TopicProgress topicProgress : topicProgressList) {
            topicMap.put(topicProgress.getTopic().getTitle(), topicProgress);
        }


        /**
         * 1. 단원 학습
         */
        String chapterInfoName = ContentConst.CHAPTER_INFO.getName();
        ChapterSection chapterInfoProgress = chapterMap.get(chapterInfoName);
        if (chapterInfoProgress == null) {
            if (chapterNum == 1) {
                chapterInfoProgress = new ChapterSection(customer, chapter, chapterInfoName, StateConst.OPEN.getName());
            }else{
                chapterInfoProgress = new ChapterSection(customer, chapter, chapterInfoName, StateConst.LOCKED.getName());
            }
            chapterSectionRepository.save(chapterInfoProgress);

        }
        contentTableList.add(new ProgressDto(chapterInfoName, title, chapterInfoProgress.getState()));

        /**
         * 2. 연표 학습
         */
        String timeFlowStudyName = ContentConst.TIME_FLOW_STUDY.getName();
        ChapterSection timeFlowStudyProgress = chapterMap.get(timeFlowStudyName);
        if (timeFlowStudyProgress == null) {
            timeFlowStudyProgress = new ChapterSection(customer, chapter, timeFlowStudyName, StateConst.LOCKED.getName());
            chapterSectionRepository.save(timeFlowStudyProgress);
        }
        contentTableList.add(new ProgressDto(timeFlowStudyName, title, timeFlowStudyProgress.getState()));


        /**
         * 3. 주제 학습
         */
        String topicStudyName = ContentConst.TOPIC_STUDY.getName();
        List<Topic> topicList = chapterValidator.checkChapter(chapterNum).getTopicList().stream()
                .sorted(Comparator.comparing(Topic::getNumber))
                .collect(Collectors.toList());
        for (Topic topic : topicList) {
            TopicProgress tp = topicMap.get(topic.getTitle());
            if (tp == null) {
                tp = new TopicProgress(customer, topic, 0, StateConst.LOCKED.getName());
                topicProgressRepository.save(tp);
            }
            contentTableList.add(new ProgressDto(topicStudyName, topic.getTitle(), tp.getState()));

        }


        /**
         * 4. 단원 마무리 학습
         */
        String chapterCompleteQuestionName = ContentConst.CHAPTER_COMPLETE_QUESTION.getName();
        ChapterSection chapterCompleteQuestionProgress = chapterMap.get(chapterCompleteQuestionName);
        if (chapterCompleteQuestionProgress == null) {
            chapterCompleteQuestionProgress = new ChapterSection(customer, chapter, chapterCompleteQuestionName, StateConst.LOCKED.getName());
            chapterSectionRepository.save(chapterCompleteQuestionProgress);
        }
        contentTableList.add(new ProgressDto(chapterCompleteQuestionName, title, chapterCompleteQuestionProgress.getState()));


        return contentTableList;
    }
}
