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

    public List<AdminChapterDto> queryTopicsInChapterAdmin(int number) {
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

        List<ChapterUserDto> chapterUserDtoList = new ArrayList<>();
        List<Tuple> ret = chapterRepository.queryChapterUserDtos(customerId);
        for (Tuple tuple : ret) {
            String chapterTitle = tuple.get(chapter.title);
            Integer chapterNum = tuple.get(chapter.number);
            String progress = tuple.get(chapterProgress.progress);

            if (progress == null) {
                Chapter chapter = checkChapter(chapterNum);
                ChapterProgress chapterProgress = new ChapterProgress(customer, chapter, ProgressConst.NOT_STARTED);
                chapterProgressRepository.save(chapterProgress);
                progress = ProgressConst.NOT_STARTED;
            }

            /**
             * state 결정
             * 1. 해당 단원을 진행한 바가 있으면 OPEN
             * 2. 해당 단원을 진행하지 않았으나 이전 단원이 COMPLETE 상태이면 OPEN
             * 3. 해당 단원을 진행하지 않고 이전 단원이 COMPLETE 상태가 아니면 CLOSED
             */

            String state;
            if (!progress.equals(ProgressConst.NOT_STARTED) || chapterNum.equals(1)) {
                state = StateConst.OPEN;
            }else {
                String prevProgress = chapterUserDtoList.get(chapterUserDtoList.size() - 1).getProgress();
                if(prevProgress.equals(ProgressConst.COMPLETE)){
                    state = StateConst.OPEN;
                }else{
                    state = StateConst.LOCKED;
                }
            }
            ChapterUserDto dto = new ChapterUserDto(chapterTitle, chapterNum, state, progress);
            chapterUserDtoList.add(dto);
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

}
