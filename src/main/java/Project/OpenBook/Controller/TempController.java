package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.topic.TopicDto;
import Project.OpenBook.Dto.topic.TopicTitleListDto;
import Project.OpenBook.Service.ChapterService;
import Project.OpenBook.Service.TopicService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class TempController {

    private final ChapterService chapterService;
    private final TopicService topicService;

    @ApiOperation(value = "각 토픽에 대한 상세정보 조회")
    @GetMapping("/topics/{topicTitle}")
    public ResponseEntity queryTopics( @PathVariable("topicTitle") String topicTitle) {
        TopicDto topicDto = topicService.queryTopic(topicTitle);
        if (topicDto==null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(topicDto, HttpStatus.OK);
    }


    @GetMapping("/chapters/{number}/topics")
    public ResponseEntity queryChpaterTopics(@PathVariable("number") int number) {
        List<Topic> topicList = chapterService.getTopicsInChapter(number);
        if (topicList == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        List<String> topicTitleList = topicList.stream().map(t -> t.getTitle()).collect(Collectors.toList());
        TopicTitleListDto dto = new TopicTitleListDto(topicTitleList);
        return new ResponseEntity(dto, HttpStatus.OK);
    }
}
