package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Sentence;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.Sentence.SentenceDto;
import Project.OpenBook.Dto.keyword.KeywordDto;
import Project.OpenBook.Dto.keyword.KeywordListDto;
import Project.OpenBook.Dto.topic.TopicDto;
import Project.OpenBook.Dto.topic.TopicTitleDto;
import Project.OpenBook.Dto.topic.TopicTitleListDto;
import Project.OpenBook.Service.ChapterService;
import Project.OpenBook.Service.TopicService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class TopicController {

    private final TopicService topicService;

    @ApiOperation(value = "각 토픽에 대한 상세정보 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 상세정보 조회 성공")
    })
    @GetMapping("/topics/{topicTitle}")
    public ResponseEntity queryTopics( @PathVariable("topicTitle") String topicTitle) {
        TopicDto topicDto = topicService.queryTopic(topicTitle);

        return new ResponseEntity(topicDto, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 토픽의 전체 키워드 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 토픽의 전체 키워드 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽 제목 입력")
    })
    @GetMapping("/topics/{topicTitle}/keywords")
    public ResponseEntity queryTopicKeywords(@PathVariable("topicTitle") String topicTitle) {
        List<KeywordDto> keywordDtoList = topicService.queryTopicKeywords(topicTitle);

        return new ResponseEntity(keywordDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 토픽의 전체 문장 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 토픽의 전체 문장 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽 제목 입력")
    })
    @GetMapping("/topics/{topicTitle}/sentences")
    public ResponseEntity queryTopicSentence(@PathVariable("topicTitle") String topicTitle) {
        List<SentenceDto> sentenceDtoList = topicService.queryTopicSentences(topicTitle).stream()
                                            .map(s -> new SentenceDto(s.getName(), s.getId()))
                                            .collect(Collectors.toList());
        return new ResponseEntity(sentenceDtoList, HttpStatus.OK);
    }




    @ApiOperation(value = "새로운 상세정보 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "상세정보 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 상세정보 생성 실패"),
    })
    @PostMapping("/admin/topics")
    public ResponseEntity createTopic(@Validated @RequestBody TopicDto topicDto) {

        Topic topic = topicService.createTopic(topicDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상세정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상세정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인해 상세정보 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상세정보 수정 시도")
    })
    @PatchMapping("/admin/topics/{topicTitle}")
    public ResponseEntity updateTopic(@PathVariable("topicTitle")String topicTitle,@Validated @RequestBody TopicDto topicDto) {

        Topic topic = topicService.updateTopic(topicTitle, topicDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "상세정보 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 삭제"),
            @ApiResponse(responseCode = "400", description = "해당 토픽에 선지/보기가 존재하는 경우"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상세정보 삭제 요청")
    })
    @DeleteMapping("/admin/topics/{topicTitle}")
    public ResponseEntity deleteTopic(@PathVariable("topicTitle") String topicTitle) {
        topicService.deleteTopic(topicTitle);
        return new ResponseEntity(HttpStatus.OK);
    }
}
