package Project.OpenBook.Service;

import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.chapter.*;
import Project.OpenBook.Dto.studyProgress.ProgressDto;
import Project.OpenBook.Dto.topic.ChapterAdminDto;
import Project.OpenBook.Dto.topic.TopicTempDto;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import com.querydsl.core.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QTopic.topic;

@Service
@Transactional
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final ChapterSectionRepository chapterSectionRepository;
    private final TopicProgressRepository topicProgressRepository;

    /**
     * 단원 저장
     * @param dto {title, number, startDate, endDate}
     * @return 저장한 단원
     * 중복된 단원 번호 -> throw CustomException(DUP_CHAPTER_NUM);
     */
    public Chapter createChapter(ChapterAddUpdateDto dto) {
        int number = dto.getNumber();
        String title = dto.getTitle();
        Integer startDate = dto.getStartDate();
        Integer endDate = dto.getEndDate();

        checkChapterNum(number);

        Chapter newChapter = new Chapter(number, title, startDate, endDate);

        chapterRepository.save(newChapter);

        return newChapter;
    }

    /**
     * 단원 전체 정보를 가져와서 List<ChapterDto>로 변경해주는 메서드
     * ChapterDto : {chapterTitle, chpaterNumber}
     * @return List<ChapterDto>
     */
    public List<ChapterDto> queryAllChapters() {
        return chapterRepository.findAll().stream()
                .map(c -> new ChapterDto(c.getTitle(), c.getNumber()))
                .collect(Collectors.toList());
    }

    /**
     * 단원에 존재하는 토픽들의 상세정보들 리턴하는 메서드
     * @param number 단원번호
     * @return List<ChapterAdminDto>
     * chapterAdminDto{categoryName, topicTitle, startDate, endDate, descriptionCount, choiceCount, keywordCount}
     */
    public List<ChapterAdminDto> queryTopicsInChapterAdmin(int number) {
        checkChapter(number);
        List<Tuple> result = topicRepository.queryAdminChapterDto(number);

        List<ChapterAdminDto> chapterAdminDtoList = new ArrayList<>();
        for (Tuple t : result) {
            String category = t.get(topic.category.name);
            String title = t.get(topic.title);
            Integer startDate = t.get(topic.startDate);
            Integer endDate = t.get(topic.endDate);
            Long descriptionCount = toDefaultCount(t.get(description.countDistinct()));
            Long choiceCount = toDefaultCount(t.get(choice.countDistinct()));
            Long keywordCount = toDefaultCount(t.get(keyword.countDistinct()));
            ChapterAdminDto chapterAdminDto = new ChapterAdminDto(category, title, startDate, endDate, descriptionCount, choiceCount, keywordCount);
            chapterAdminDtoList.add(chapterAdminDto);
        }

        return chapterAdminDtoList;
    }

    private Long toDefaultCount(Long count) {
        return count != null ? count : 0L;
    }

    /**
     * 단원 정보를 갱신하는 메서드  
     * @param num 기존 단원번호
     * @param chapterAddUpdateDto {title, number, startDate, endDate}
     * @return
     */
    public Chapter updateChapter(int num, ChapterAddUpdateDto chapterAddUpdateDto) {
        int inputNumber = chapterAddUpdateDto.getNumber();
        if(num != inputNumber){
            checkChapterNum(inputNumber);
        }
        Chapter chapter = checkChapter(num);

        Chapter updateChapter = chapter.updateChapter(chapterAddUpdateDto.getTitle(), chapterAddUpdateDto.getNumber(),
                chapterAddUpdateDto.getStartDate(), chapterAddUpdateDto.getEndDate());
        return updateChapter;
    }

    /**
     * 단원을 삭제하는 메서드
     * @param num 단원 번호
     * @return Boolean값
     * 존재하지 않는 단원 번호 -> CHAPTER_NOT_FOUND
     * 해당 단원에 토픽이 존재하는 경우 -> CHAPTER_HAS_TOPIC
     */
    public Boolean deleteChapter(int num) {
        Chapter chapter = checkChapter(num);

        //해당 단원에 토픽이 존재하는 경우 단원 삭제 불가능
        List<Topic> topicList = topicRepository.findAllByChapter(chapter);
        if(!topicList.isEmpty()){
            throw new CustomException(CHAPTER_HAS_TOPIC);
        }

        chapterRepository.delete(chapter);
        return true;
    }
    public ChapterTitleDto queryChapterTitle(Integer num) {
        Chapter chapter = checkChapter(num);
        return new ChapterTitleDto(chapter.getTitle());
    }

    public ChapterInfoDto queryChapterInfoAdmin(Integer num) {
        Chapter chapter = checkChapter(num);
        return new ChapterInfoDto(chapter.getContent());
    }

    public ChapterTitleInfoDto queryChapterInfoCustomer(Integer num) {
        Chapter chapter = checkChapter(num);

        return new ChapterTitleInfoDto(chapter.getTitle(), chapter.getContent());
    }


    public ChapterTitleInfoDto queryChapterTitleInfo(Integer num) {
        Chapter chapter = checkChapter(num);

        return new ChapterTitleInfoDto(chapter.getTitle(), chapter.getContent());
    }


    /**
     * chapterInfo를 update하는 메서드
     * @param num 단원 번호
     * @param content 새로운 chapterInfo
     * @return 변경된 chapterInfo를 가지는 chapterInfoDto
     */
    public ChapterInfoDto updateChapterInfo(Integer num, String content) {
        Chapter chapter = checkChapter(num);

        chapter.updateContent(content);
        return new ChapterInfoDto(chapter.getContent());
    }

    /**
     * 단원 전체 정보와 회원별 progress,state를 리턴하는 메서드
     * @param customer 회원정보
     * @return {chapter.title, chapter.number, state, progress}
     * 1단원의 경우 default => state = Open, progress = ChapterInfo
     * 2단원 이후의 경우 default => state = Locked, progress = NotStarted
     */
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

    public List<TopicTempDto> queryTopicsInChapterCustomer(int num) {
        checkChapter(num);
        List<TopicTempDto> dtoList = new ArrayList<>();
        List<Tuple> tupleList = topicRepository.queryTopicForTopicTempDto(num);
        for (Tuple tuple : tupleList) {
            String title = tuple.get(topic.title);
            String category = tuple.get(topic.category.name);
            Integer startDate = tuple.get(topic.startDate);
            Integer endDate = tuple.get(topic.endDate);
            dtoList.add(new TopicTempDto(title, category, startDate, endDate));
        }

        return dtoList;
    }

    /**
     * 목차 제공하는 메서드
     * @param customer 회원정보
     * @param chapterNum 단원번호
     * @return {content, title(단원이면 단원제목, 주제면 주제제목), state}
     * 특정 chapterSection이 존재하지 않는 경우 생성해서 제공
     * 존재하지 않는 chapterNum 입력 -> CHAPTER_NOT_FOUND Exception
     */
    public List<ProgressDto> queryContentTable(Customer customer, Integer chapterNum) {
        Chapter chapter = checkChapter(chapterNum);
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
        for (TopicProgress topicProgress : topicProgressList) {
            contentTableList.add(new ProgressDto(topicStudyName, topicProgress.getTopic().getTitle(), topicProgress.getState()));
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

    private void checkChapterNum(int number) {
        chapterRepository.findOneByNumber(number).ifPresent(c -> {
            throw new CustomException(DUP_CHAPTER_NUM);
        });
    }

    private Chapter checkChapter(int num) {
        return chapterRepository.findOneByNumber(num).orElseThrow(() -> {
            throw new CustomException(CHAPTER_NOT_FOUND);
        });
    }

    private Topic checkTopic(String topicTitle) {
        return topicRepository.findTopicByTitle(topicTitle).orElseThrow(() -> {
            throw new CustomException(TOPIC_NOT_FOUND);
        });
    }

    public List<ChapterContentDto> queryChapters() {
        Map<Chapter, Long> map = chapterRepository.queryChapterContentDto();
        List<ChapterContentDto> dtoList = new ArrayList<>();
        for (Chapter chapter : map.keySet()) {
            Long topicCount = map.get(chapter);
            ChapterContentDto dto = new ChapterContentDto(chapter.getTitle(), chapter.getNumber(), chapter.getStartDate(), chapter.getEndDate(), topicCount);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
