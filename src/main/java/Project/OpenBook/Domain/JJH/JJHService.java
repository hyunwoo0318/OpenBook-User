package Project.OpenBook.Domain.JJH;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Chapter.Repo.ChapterRepository;
import Project.OpenBook.Domain.TimeLine.Timeline;
import Project.OpenBook.Domain.TimeLine.TimelineQueryDto;
import Project.OpenBook.Domain.TimeLine.TimelineRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JJHService {

    private final ChapterRepository chapterRepository;
    private final TimelineRepository timelineRepository;

    @Transactional(readOnly = true)
    public JJHQueryListDto queryJJH() {
        List<ChapterJJHQueryDto> chapterList = chapterRepository.findAll().stream()
                .map(c -> new ChapterJJHQueryDto(c.getNumber(), c.getJjhListNumber(), c.getTitle(), c.getId()))
                .sorted(Comparator.comparing(ChapterJJHQueryDto::getNumber))
                .collect(Collectors.toList());
        List<TimelineQueryDto> timelineList = timelineRepository.queryTimelinesWithEra().stream()
                .map(t -> new TimelineQueryDto(t.getEra().getName(), t.getStartDate(), t.getEndDate(), t.getJjhListNumber(), t.getId()))
                .sorted(Comparator.comparing(TimelineQueryDto::getStartDate))
                .collect(Collectors.toList());

        return new JJHQueryListDto(chapterList, timelineList);
    }

    @Transactional
    public void updateJJHList(JJHListUpdateDto dto) {
        List<jjhUpdateDto> chapterList = dto.getChapterList();
        List<jjhUpdateDto> timelineList = dto.getTimelineList();

        Map<Long, Chapter> chapterMap = chapterRepository.findAll().stream()
                .collect(Collectors.toMap(Chapter::getId, ch -> ch));
        Map<Long, Timeline> timelineMap = timelineRepository.findAll().stream()
                .collect(Collectors.toMap(Timeline::getId, t -> t));

        for (jjhUpdateDto chapterDto : chapterList) {
            Chapter chapter = chapterMap.get(chapterDto.getId());
            if (chapter == null) {
                throw new CustomException(ErrorCode.CHAPTER_NOT_FOUND);
            }
            chapter.updateJJHListNumber(chapterDto.getJjhNumber());
        }

        for (jjhUpdateDto timelineDto : timelineList) {
            Timeline timeline = timelineMap.get(timelineDto.getId());
            if (timeline == null) {
                throw new CustomException(ErrorCode.TIMELINE_NOT_FOUND);
            }
            timeline.updateJJHListNumber(timelineDto.getJjhNumber());
        }

        /**
         * TODO : 추가 컨텐츠 목록 갱신
         */
    }
}
