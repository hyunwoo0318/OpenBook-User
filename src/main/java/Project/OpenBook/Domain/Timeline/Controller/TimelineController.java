package Project.OpenBook.Domain.Timeline.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Timeline.Service.Dto.TimelineQueryCustomerDto;
import Project.OpenBook.Domain.Timeline.Service.TimelineService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;


    //TODO : timeline count 조회
    @GetMapping("/time-lines")
    public ResponseEntity queryTimelinesCustomer(@Parameter(hidden = true) @AuthenticationPrincipal Customer customer) {
        List<TimelineQueryCustomerDto> dtoList = new ArrayList<>();
        if (customer == null) {
            dtoList = timelineService.queryTimelinesForFree();
        }else{
            dtoList = timelineService.queryTimelinesCustomer(customer);
        }
        return new ResponseEntity(dtoList, HttpStatus.OK);
    }



}
