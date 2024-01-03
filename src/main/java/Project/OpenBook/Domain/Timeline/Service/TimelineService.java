package Project.OpenBook.Domain.Timeline.Service;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Repo.TimelineLearningRecordRepository;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Timeline.Service.Dto.TimelineQueryCustomerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TimelineService {
    private final TimelineRepository timelineRepository;
    private final TimelineLearningRecordRepository timelineLearningRecordRepository;



    @Transactional(readOnly = true)
    public List<TimelineQueryCustomerDto> queryTimelinesCustomer(Customer customer) {
        return timelineLearningRecordRepository.queryTimelineLearningRecord(customer).stream()
                .map(TimelineQueryCustomerDto::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TimelineQueryCustomerDto> queryTimelinesForFree() {
        return timelineRepository.findAll().stream()
                .map(TimelineQueryCustomerDto::new)
                .collect(Collectors.toList());
    }




}
