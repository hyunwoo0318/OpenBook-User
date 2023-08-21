package Project.OpenBook.Service;

import Project.OpenBook.Constants.ContentConst;
import Project.OpenBook.Constants.ProgressConst;
import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.chapter.*;
import Project.OpenBook.Dto.studyProgress.ProgressDto;
import Project.OpenBook.Dto.topic.ChapterAdminDto;
import Project.OpenBook.Repository.chapterProgress.ChapterProgressRepository;
import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.QChapterProgress.chapterProgress;
import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QSentence.sentence;
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
     * @param title 단원 제목
     * @param number 단원 번호
     * @return 저장한 단원
     * 중복된 단원 번호 -> throw CustomException(DUP_CHAPTER_NUM);
     */
    public Chapter createChapter(String title, int number) {
        checkChapterNum(number);

        Chapter newChapter = Chapter.builder()
                .number(number)
                .title(title)
                .build();

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
            Long descriptionCount = t.get(description.countDistinct());
            if(descriptionCount == null) descriptionCount = 0L;
            Long choiceCount = t.get(choice.countDistinct());
            if(choiceCount == null) choiceCount = 0L;
            Long keywordCount = t.get(keyword.countDistinct());
            if(keywordCount == null) keywordCount = 0L;
            ChapterAdminDto chapterAdminDto = new ChapterAdminDto(category, title, startDate, endDate, descriptionCount, choiceCount, keywordCount);
            chapterAdminDtoList.add(chapterAdminDto);
        }

        return chapterAdminDtoList;
    }

    /**
     * 단원 정보를 갱신하는 메서드  
     * @param num 단원번호  
     * @param inputTitle 변경할 단원제목
     * @param inputNum 변경할 단원번호
     * @return
     */
    public Chapter updateChapter(int num, String inputTitle, int inputNum) {
        if(num != inputNum){
            checkChapterNum(inputNum);
        }
        Chapter chapter = checkChapter(num);

        Chapter updateChapter = chapter.updateChapter(inputTitle, inputNum);
        return updateChapter;
    }

    public Boolean deleteChapter(int num) {
        Chapter chapter = checkChapter(num);

        //해당 단원에 토픽이 존재하는 경우 단원 삭제 불가능
        List<Topic> topicList = topicRepository.findAllByChapter(chapter);
        if(!topicList.isEmpty()){
            throw new CustomException(CHAPTER_HAS_TOPIC);
        }

        List<ChapterSection> chapterSectionList = chapterSectionRepository.queryChapterSection(num);
        chapterSectionRepository.deleteAllInBatch(chapterSectionList);

        List<ChapterProgress> chapterProgressList = chapterProgressRepository.queryChapterProgress(num);
        chapterProgressRepository.deleteAllInBatch(chapterProgressList);
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

    public ChapterInfoDto updateChapterInfo(Integer num, String content) {
        Chapter chapter = checkChapter(num);

        chapter.updateContent(content);
        return new ChapterInfoDto(chapter.getContent());
    }

    public ChapterTitleInfoDto queryChapterTitleInfo(Integer num) {
        Chapter chapter = checkChapter(num);

        return new ChapterTitleInfoDto(chapter.getTitle(), chapter.getContent());
    }

    public List<ChapterUserDto> queryChapterUserDtos(Customer customer) {
        Long customerId = customer.getId();

        List<ChapterUserDto> chapterUserDtoList = new ArrayList<>();
        Map<Chapter, Group> map = chapterRepository.queryChapterUserDtos(customerId);
        for (Chapter chapter : map.keySet()) {
            Group group = map.get(chapter);
            ChapterProgress findChapterProgress = group.getOne(QChapterProgress.chapterProgress);
            List<ChapterSection> chapterSectionList = group.getList(QChapterSection.chapterSection);

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

    public List<String> queryTopicsInChapterCustomer(int num) {
        checkChapter(num);
        return topicRepository.queryTopicTitleCustomer(num);
    }

    /**
     * 단원별 목차 제공
     * 단원 학습 -> 연표 학습 -> 연표 문제 -> { 주제학습 ->  키워드 보고 주제 맞추기 -> 문장 보고 주제맞추기 -> 키워드 보고 주제 맞추기 } -> 문장 보고 주제 맞추기
     *
     */
    public List<ProgressDto> queryContentTable(Customer customer, Integer number) {
        Chapter chapter = checkChapter(number);
        String title = chapter.getTitle();
        HashMap<String, ChapterSection> chapterMap = new HashMap<>();
        HashMap<String, TopicProgress> topicMap = new HashMap<>();
        List<ProgressDto> contentTableList = new ArrayList<>();
        chapterSectionRepository.queryChapterSection(customer.getId(), number).stream()
                .map(cp -> chapterMap.put(cp.getContent(), cp));
        topicProgressRepository.queryTopicProgresses(customer.getId(), number).stream()
                .map(tp -> topicMap.put(tp.getContent(), tp));

        /**
         * 1. 단원 학습
         */
        String chapterInfoName = ContentConst.CHAPTER_INFO.getName();
        ChapterSection chapterInfoProgress = chapterMap.get(chapterInfoName);
        if (chapterInfoProgress == null) {
            chapterInfoProgress = new ChapterSection(customer, chapter, chapterInfoName, StateConst.OPEN.getName());
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
         * 3. 연표 문제
         */
        String timeFlowQuestionName = ContentConst.TIME_FLOW_QUESTION.getName();
        ChapterSection timeFlowQuestionProgress = chapterMap.get(timeFlowQuestionName);
        if (timeFlowQuestionProgress == null) {
            timeFlowQuestionProgress = new ChapterSection(customer, chapter, timeFlowQuestionName, StateConst.LOCKED.getName());
            chapterSectionRepository.save(timeFlowQuestionProgress);
        }
        contentTableList.add(new ProgressDto(timeFlowQuestionName, title, timeFlowQuestionProgress.getState()));

        /**
         * 주제
         */
        List<Tuple> tuples = topicRepository.queryTopicForTable(number);
        for (Tuple tuple : tuples) {
            String topicTitle = tuple.get(topic.title);
            Long keywordNum = tuple.get(keyword.countDistinct());
            Long sentenceNum = tuple.get(sentence.countDistinct());

            //TODO : State값 찾아서 적용
            contentTableList.add(new ProgressDto(ContentConst.TOPIC_STUDY.getName(), topicTitle, StateConst.OPEN.getName()));
            if(keywordNum != null && keywordNum >= 1 ){
                contentTableList.add(new ProgressDto(ContentConst.GET_KEYWORD_BY_TOPIC.getName(), topicTitle, StateConst.OPEN.getName()));
            }
            if(sentenceNum != null && sentenceNum >= 1 ){
                contentTableList.add(new ProgressDto(ContentConst.GET_SENTENCE_BY_TOPIC.getName(), topicTitle, StateConst.OPEN.getName()));
            }
        }

        /**
         * 키워드 보고 주제 맞추기
         */
        String getTopicByKeywordName = ContentConst.GET_TOPIC_BY_KEYWORD.getName();
        ChapterSection getTopicByKeywordProgress = chapterMap.get(getTopicByKeywordName);
        if (getTopicByKeywordProgress == null) {
            getTopicByKeywordProgress = new ChapterSection(customer, chapter, getTopicByKeywordName, StateConst.LOCKED.getName());
            chapterSectionRepository.save(getTopicByKeywordProgress);
        }
        contentTableList.add(new ProgressDto(getTopicByKeywordName, title, getTopicByKeywordProgress.getState()));

        /**
         * 문장 보고 주제 맞추기
         */
        String getTopicBySentenceName = ContentConst.GET_TOPIC_BY_SENTENCE.getName();
        ChapterSection getTopicBySentenceProgress = chapterMap.get(getTopicBySentenceName);
        if (getTopicBySentenceProgress == null) {
            getTopicBySentenceProgress = new ChapterSection(customer, chapter, getTopicBySentenceName, StateConst.LOCKED.getName());
            chapterSectionRepository.save(getTopicBySentenceProgress);
        }
        contentTableList.add(new ProgressDto(getTopicBySentenceName, title, getTopicBySentenceProgress.getState()));


        return contentTableList;
    }
}
