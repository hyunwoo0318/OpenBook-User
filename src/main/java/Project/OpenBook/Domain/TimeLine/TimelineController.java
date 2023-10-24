package Project.OpenBook.Domain.TimeLine;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TimelineController {

    private final TimelineService timelineService;

    @GetMapping("/time-lines")
    public ResponseEntity queryTimelines() {
        List<TimelineQueryDto> dtoList = timelineService.queryTimelines();
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