package Project.OpenBook.Domain.Timeline.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Timeline.Service.Dto.TimelineAddUpdateDto;
import Project.OpenBook.Domain.Timeline.Service.Dto.TimelineQueryAdminDto;
import Project.OpenBook.Domain.Timeline.Service.Dto.TimelineQueryCustomerDto;
import Project.OpenBook.Domain.Timeline.Service.TimelineService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping("/admin/time-lines")
    public ResponseEntity queryTimelinesAdmin() {
        List<TimelineQueryAdminDto> dtoList = timelineService.queryTimelinesAdmin();
        return new ResponseEntity(dtoList, HttpStatus.OK);
    }

    //TODO
    @GetMapping("/time-lines")
    public ResponseEntity queryTimelinesCustomer(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer) {
        List<TimelineQueryCustomerDto> dtoList = timelineService.queryTimelinesCustomer(customer);
        return new ResponseEntity(dtoList, HttpStatus.OK);
    }


    @PostMapping("/admin/time-lines")
    public ResponseEntity addTimeline(@Validated @RequestBody TimelineAddUpdateDto dto) {
        timelineService.addTimeline(dto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @PatchMapping("/admin/time-lines/{id}")
    public ResponseEntity updateTimeline(@Validated @RequestBody TimelineAddUpdateDto dto,
                                         @PathVariable("id") Long id) {
        timelineService.updateTimeline(dto,id);
        return new ResponseEntity(HttpStatus.OK);
    }

    @DeleteMapping("/admin/time-lines/{id}")
    public ResponseEntity updateTimeline(@PathVariable("id") Long id) {
        timelineService.deleteTimeline(id);
        return new ResponseEntity(HttpStatus.OK);
    }
}
