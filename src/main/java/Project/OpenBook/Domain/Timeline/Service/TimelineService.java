package Project.OpenBook.Domain.Timeline.Service;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Era.Era;
import Project.OpenBook.Domain.Era.EraRepository;
import Project.OpenBook.Domain.Timeline.Domain.Timeline;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Timeline.Service.Dto.TimelineAddUpdateDto;
import Project.OpenBook.Domain.Timeline.Service.Dto.TimelineQueryAdminDto;
import Project.OpenBook.Domain.Timeline.Service.Dto.TimelineQueryCustomerDto;
import Project.OpenBook.Domain.TimelineLearningRecord.Repo.TimelineLearningRecordRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.ERA_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.TIMELINE_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class TimelineService {
    private final TimelineRepository timelineRepository;
    private final TimelineLearningRecordRepository timelineLearningRecordRepository;
    private final EraRepository eraRepository;

    @Transactional(readOnly = true)
    public List<TimelineQueryAdminDto> queryTimelinesAdmin() {
        return timelineRepository.queryTimelinesWithEra().stream()
                .map(t -> new TimelineQueryAdminDto(t.getTitle(), t.getEra().getName(), t.getStartDate(), t.getEndDate(), t.getId()))
                .sorted(Comparator.comparing(TimelineQueryAdminDto::getStartDate))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimelineQueryCustomerDto> queryTimelinesCustomer(Customer customer) {
        return timelineLearningRecordRepository.queryTimelineLearningRecord(customer).stream()
                .map(TimelineQueryCustomerDto::new)
                .collect(Collectors.toList());

    }

    @Transactional
    public void addTimeline(TimelineAddUpdateDto dto) {
        String eraName = dto.getEra();
        Era era = eraRepository.findByName(eraName).orElseThrow(() -> {
            throw new CustomException(ERA_NOT_FOUND);
        });

        Timeline timeline = new Timeline(dto.getTitle(),dto.getStartDate(), dto.getEndDate(), era);
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

        timeline.updateTimeline(dto.getTitle(), dto.getStartDate(), dto.getEndDate(),era);
    }

    @Transactional
    public void deleteTimeline(Long id) {
        timelineRepository.deleteById(id);
    }



}
