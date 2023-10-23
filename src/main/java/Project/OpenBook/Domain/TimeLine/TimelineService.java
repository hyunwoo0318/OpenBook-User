package Project.OpenBook.Domain.TimeLine;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Domain.Era.Era;
import Project.OpenBook.Domain.Era.EraRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.ERA_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.TIMELINE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TimelineService {
    private final TimelineRepository timelineRepository;
    private final EraRepository eraRepository;

    @Transactional(readOnly = true)
    public List<TimelineQueryDto> queryTimelines() {
        return timelineRepository.queryTimelinesWithEra().stream()
                .map(t -> new TimelineQueryDto(t.getEra().getName(), t.getStartDate(), t.getEndDate(), t.getJjhListNumber(),t.getId()))
                .sorted(Comparator.comparing(TimelineQueryDto::getStartDate))
                .collect(Collectors.toList());
    }

    @Transactional
    public void addTimeline(TimelineAddUpdateDto dto) {
        String eraName = dto.getEra();
        Era era = eraRepository.findByName(eraName).orElseThrow(() -> {
            throw new CustomException(ERA_NOT_FOUND);
        });

        Timeline timeline = new Timeline(dto.getStartDate(), dto.getEndDate(), era);
        timelineRepository.save(timeline);
    }

    @Transactional
    public void updateTimeline(TimelineAddUpdateDto dto, Long id) {
        String eraName = dto.getEra();
        Era era = eraRepository.findByName(eraName).orElseThrow(() -> {
            throw new CustomException(ERA_NOT_FOUND);
        });

        Timeline timeline = timelineRepository.findById(id).orElseThrow(() -> {
            throw new CustomException(TIMELINE_NOT_FOUND);
        });

        timeline.updateTimeline(dto.getStartDate(), dto.getEndDate(),era);
    }

    @Transactional
    public void deleteTimeline(Long id) {
        timelineRepository.deleteById(id);
    }


}
