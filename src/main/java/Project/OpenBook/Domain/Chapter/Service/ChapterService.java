package Project.OpenBook.Domain.Chapter.Service;

import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterAddUpdateDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterInfoDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterNumberUpdateDto;
import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearch;
import Project.OpenBook.Domain.Search.ChapterSearch.ChapterSearchRepository;
import Project.OpenBook.Domain.Topic.Domain.Topic;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Image.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.*;

import static Project.OpenBook.Constants.ErrorCode.CHAPTER_HAS_TOPIC;
import static Project.OpenBook.Constants.ErrorCode.CHAPTER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final ChapterSearchRepository chapterSearchRepository;
    private final ChapterValidator chapterValidator;
    private final ImageService imageService;


    /**
     * 단원 저장
     * @param dto {title, number, dateComment}
     * @return 저장한 단원
     * 중복된 단원 번호 -> throw CustomException(DUP_CHAPTER_NUM);
     */

    @Transactional
    public Chapter createChapter(ChapterAddUpdateDto dto) {
        int number = dto.getNumber();
        String title = dto.getTitle();
        String dateComment = dto.getDateComment();

        chapterValidator.checkDupChapterNum(number);

        Chapter newChapter = new Chapter(number, dateComment, title);

        Chapter chapter = chapterRepository.save(newChapter);
        chapterSearchRepository.save(new ChapterSearch(chapter));

        return newChapter;
    }


    /**
     * 단원 정보를 갱신하는 메서드  
     * @param num 기존 단원번호
     * @param chapterAddUpdateDto {title, number, startDate, endDate}
     * @return
     */
    @Transactional
    public Chapter updateChapter(int num, ChapterAddUpdateDto chapterAddUpdateDto) {
        int inputNumber = chapterAddUpdateDto.getNumber();
        if(num != inputNumber){
            chapterValidator.checkDupChapterNum(inputNumber);
        }
        Chapter chapter = chapterValidator.checkChapter(num);

        Chapter updatedChapter = chapter.updateChapter(chapterAddUpdateDto.getTitle(), chapterAddUpdateDto.getNumber(),
                chapterAddUpdateDto.getDateComment());
        chapterSearchRepository.save(new ChapterSearch(updatedChapter));
        return updatedChapter;
    }

    /**
     * 단원을 삭제하는 메서드
     * @param num 단원 번호
     * @return Boolean값
     * 존재하지 않는 단원 번호 -> CHAPTER_NOT_FOUND
     * 해당 단원에 토픽이 존재하는 경우 -> CHAPTER_HAS_TOPIC
     */
    @Transactional
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
    @Transactional
    public ChapterInfoDto updateChapterInfo(Integer num, String content) throws IOException {
        Chapter chapter = chapterValidator.checkChapter(num);

        if(content != null && content.isBlank()){
            content = null;
        }
        else if(content != null && !content.isBlank() &&!content.startsWith("https"))
        {
            imageService.checkBase64(content);
            content = imageService.storeFile(content);
        }
        chapter.updateContent(content);
        return new ChapterInfoDto(chapter.getContent());
    }

    @Transactional
    public void updateChapterNumber(List<ChapterNumberUpdateDto> chapterNumberUpdateDtoList) {
        Map<Long, Integer> m = new HashMap<>();
        Set<Integer> numberSet = new HashSet<>();
        Set<Long> idSet = new HashSet<>();
        for (ChapterNumberUpdateDto dto : chapterNumberUpdateDtoList) {
            m.put(dto.getId(), dto.getNumber()+1);
            numberSet.add(dto.getNumber()+1);
            idSet.add(dto.getId());
        }

        if (idSet.size() != chapterNumberUpdateDtoList.size() || numberSet.size() != chapterNumberUpdateDtoList.size()) {
            throw new CustomException(CHAPTER_NOT_FOUND);
        }

        List<Chapter> chapterList = chapterRepository.findAll();
        chapterList.forEach(c -> c.updateNumber(-c.getNumber()));

        for (Chapter chapter : chapterList) {
            Integer chapterNumber = m.get(chapter.getId());
            if (chapterNumber == null) {
                throw new CustomException(CHAPTER_NOT_FOUND);
            }
            chapter.updateNumber(chapterNumber);
        }



    }
}
