package Project.OpenBook.Service;

import Project.OpenBook.Constants.ProgressConst;
import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.*;
import Project.OpenBook.Dto.chapter.*;
import Project.OpenBook.Dto.topic.AdminChapterDto;
import Project.OpenBook.Repository.chapterprogress.ChapterProgressRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.group.Group;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.status.StatusConsoleListener;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static Project.OpenBook.Constants.ErrorCode.*;
import static Project.OpenBook.Domain.QChapter.chapter;
import static Project.OpenBook.Domain.QChapterProgress.chapterProgress;
import static Project.OpenBook.Domain.QChoice.choice;
import static Project.OpenBook.Domain.QDescription.description;
import static Project.OpenBook.Domain.QKeyword.keyword;
import static Project.OpenBook.Domain.QTopic.topic;
import static com.querydsl.core.group.GroupBy.list;

@Service
@Transactional
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;
    private final CustomerRepository customerRepository;
    private final ChapterProgressRepository chapterProgressRepository;
    private final StudyProgressService studyProgressService;


    public Chapter createChapter(String title, int number) {
        checkChapterNum(number);

        Chapter newChapter = Chapter.builder()
                .number(number)
                .title(title)
                .build();

        chapterRepository.save(newChapter);
        updateChapterProgress(newChapter);

        return newChapter;
    }

    private void updateChapterProgress(Chapter newChapter) {
        List<ChapterProgress> chapterProgressList = customerRepository.findAll().stream()
                .map(c -> new ChapterProgress(c, newChapter))
                .collect(Collectors.toList());
        chapterProgressRepository.saveAll(chapterProgressList);
    }

    public List<ChapterDto> queryAllChapters() {
        return chapterRepository.findAll().stream()
                .map(c -> new ChapterDto(c.getTitle(), c.getNumber()))
                .collect(Collectors.toList());
    }

    public List<AdminChapterDto> queryTopicsInChapter(int number) {
        checkChapter(number);
        List<Tuple> result = topicRepository.queryAdminChapterDto(number);

        List<AdminChapterDto> adminChapterDtoList = new ArrayList<>();
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
            AdminChapterDto adminChapterDto = new AdminChapterDto(category, title, startDate, endDate, descriptionCount, choiceCount, keywordCount);
            adminChapterDtoList.add(adminChapterDto);
        }

        return adminChapterDtoList;
    }

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

    public ChapterInfoDto queryChapterInfoCustomer(Long customerId,Integer num) {
        Chapter chapter = checkChapter(num);

        //progress update
        studyProgressService.updateProgress(customerId, num, ProgressConst.CHAPTER_INFO);

        return new ChapterInfoDto(chapter.getContent());
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

    public void updateChapterInfo(Integer num, String content) {
        Chapter chapter = checkChapter(num);

        chapter.updateContent(content);
    }

    public ChapterTitleInfoDto queryChapterTitleInfo(Integer num) {
        Chapter chapter = checkChapter(num);

        return new ChapterTitleInfoDto(chapter.getTitle(), chapter.getContent());
    }

    public List<ChapterUserDto> queryChapterUserDtos(Customer customer) {
        Long customerId = customer.getId();

        //ChapterProgress가 오류로 인해 없는경우 고려
        int totalChapterCnt = chapterRepository.findAll().size();
        List<ChapterProgress> customerChapterProgressList = chapterProgressRepository.queryChapterProgress(customerId);
        if(totalChapterCnt != customerChapterProgressList.size()){
            Set<Integer> chapterNumSet = customerChapterProgressList.stream()
                    .map(c -> c.getChapter().getNumber())
                    .collect(Collectors.toSet());

            for (int i = 1; i <= totalChapterCnt; i++) {
                if(!chapterNumSet.contains(i)){
                    Chapter chapter = checkChapter(i);
                    ChapterProgress chapterProgress = new ChapterProgress(customer, chapter);
                    chapterProgressRepository.save(chapterProgress);
                }
            }
        }

        Map<Integer, Group> map = chapterRepository.queryChapterUserDtos(customerId);
        List<ChapterUserDto> chapterUserDtoList = new ArrayList<>();
        for (Integer chapterNum : map.keySet()) {
            Group group = map.get(chapterNum);
            String title = group.getOne(chapter.title);
            Integer number = group.getOne(chapter.number);
            String progress = group.getOne(chapterProgress.progress);
            String status = getStatus(progress);
            List<String> topicList = group.getList(topic).stream()
                    .sorted(Comparator.comparing(Topic::getNumber))
                    .map(Topic::getTitle)
                    .collect(Collectors.toList());
            ChapterUserDto chapterUserDto = new ChapterUserDto(title, number, status, progress, topicList);
            chapterUserDtoList.add(chapterUserDto);
        }

        //단원순으로 정렬
        List<ChapterUserDto> sortedChapterUserDtoList = chapterUserDtoList.stream()
                .sorted(Comparator.comparing(ChapterUserDto::getNumber))
                .collect(Collectors.toList());

        return sortedChapterUserDtoList;
    }

    private String getStatus(String progress) {
        if(progress.equals(ProgressConst.NOT_STARTED)){
            return StateConst.NOT_STARTED;
        }else if(progress.equals(ProgressConst.GET_TOPIC_BY_SENTENCE)){
            return StateConst.DONE;
        }else{
            return StateConst.IN_PROGRESS;
        }
    }
}
