package Project.OpenBook.Chapter.Service;

import Project.OpenBook.Chapter.ChapterValidator;
import Project.OpenBook.Chapter.Controller.dto.ChapterAddUpdateDto;
import Project.OpenBook.Chapter.Controller.dto.ChapterInfoDto;
import Project.OpenBook.Chapter.Domain.Chapter;
import Project.OpenBook.Topic.Domain.Topic;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Topic.Repo.TopicRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final ChapterValidator chapterValidator;

    /**
     * 단원 저장
     * @param dto {title, number, startDate, endDate}
     * @return 저장한 단원
     * 중복된 단원 번호 -> throw CustomException(DUP_CHAPTER_NUM);
     */

    @Transactional
    public Chapter createChapter(ChapterAddUpdateDto dto) {
        int number = dto.getNumber();
        String title = dto.getTitle();
        Integer startDate = dto.getStartDate();
        Integer endDate = dto.getEndDate();

        chapterValidator.checkDupChapterNum(number);

        Chapter newChapter = new Chapter(number, title, startDate, endDate);

        chapterRepository.save(newChapter);

        return newChapter;
    }

    /**
     * 단원 전체를 조회하는 메서드
     * @return List<Chapter>
     */
    public List<Chapter> queryChapters() {
        return chapterRepository.findAll();
    }

    /**
     * 특정 단원을 조회하는 메서드
     * @param num 단원 번호
     * @return Chapter
     * 해당 단원 번호를 가진 단원이 존재하지 않는다면 -> CHAPTER_NOT_FOUND
     */
    public Chapter queryChapter(Integer num) {
        return chapterValidator.checkChapter(num);
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
            chapterValidator.checkDupChapterNum(inputNumber);
        }
        Chapter chapter = chapterValidator.checkChapter(num);

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
        Chapter chapter = chapterValidator.checkChapter(num);

        //해당 단원에 토픽이 존재하는 경우 단원 삭제 불가능
        List<Topic> topicList = chapter.getTopicList();
        if(!topicList.isEmpty()){
            throw new CustomException(CHAPTER_HAS_TOPIC);
        }

        chapterRepository.delete(chapter);
        return true;
    }


    /**
     * chapterInfo를 update하는 메서드
     * @param num 단원 번호
     * @param content 새로운 chapterInfo
     * @return 변경된 chapterInfo를 가지는 chapterInfoDto
     */
    public ChapterInfoDto updateChapterInfo(Integer num, String content) {
        Chapter chapter = chapterValidator.checkChapter(num);

        chapter.updateContent(content);
        return new ChapterInfoDto(chapter.getContent());
    }



}
