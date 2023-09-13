package Project.OpenBook.Domain.Topic.Controller;

import Project.OpenBook.Domain.Topic.Service.dto.TopicWithKeywordSentenceDto;
import Project.OpenBook.Domain.Sentence.Dto.SentenceDto;
import Project.OpenBook.Domain.Choice.Dto.ChoiceDto;
import Project.OpenBook.Domain.Description.Dto.DescriptionDto;
import Project.OpenBook.Domain.Keyword.Dto.KeywordDto;
import Project.OpenBook.Domain.Topic.Service.TopicSimpleQueryService;
import Project.OpenBook.Domain.Topic.Service.dto.TopicDetailDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicNumberDto;
import Project.OpenBook.Domain.Topic.Service.TopicService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class TopicController {

    private final TopicService topicService;
    private final TopicSimpleQueryService topicSimpleQueryService;

    @ApiOperation(value = "각 토픽에 대한 상세정보 조회 - 관리자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 상세정보 조회 성공")
    })
    @GetMapping("admin/topics/{topicTitle}")
    public ResponseEntity<TopicDetailDto> queryTopicsAdmin(@PathVariable("topicTitle") String topicTitle) {
        TopicDetailDto dto = topicSimpleQueryService.queryTopicsAdmin(topicTitle);

        return new ResponseEntity<TopicDetailDto>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "각 토픽에 대한 상세정보 조회 - 사용자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 상세정보 조회 성공")
    })
    @GetMapping("/topics/{topicTitle}")
    @Transactional
    public ResponseEntity<TopicWithKeywordSentenceDto> queryTopicsTitle(@PathVariable("topicTitle") String topicTitle) {
        TopicWithKeywordSentenceDto dto = topicSimpleQueryService.queryTopicsCustomer(topicTitle);

        return new ResponseEntity<TopicWithKeywordSentenceDto>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 토픽의 전체 키워드 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 토픽의 전체 키워드 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽 제목 입력")
    })
    @GetMapping("/topics/{topicTitle}/keywords")
    public ResponseEntity<List<KeywordDto>> queryTopicKeywords(@PathVariable("topicTitle") String topicTitle) {
        List<KeywordDto> dtoList = topicSimpleQueryService.queryTopicKeywords(topicTitle);

        return new ResponseEntity<List<KeywordDto>>(dtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 토픽의 전체 문장 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 토픽의 전체 문장 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽 제목 입력")
    })
    @GetMapping("/topics/{topicTitle}/sentences")
    public ResponseEntity<List<SentenceDto>> queryTopicSentence(@PathVariable("topicTitle") String topicTitle) {
        List<SentenceDto> dtoList = topicSimpleQueryService.queryTopicSentences(topicTitle);
        return new ResponseEntity<List<SentenceDto>>(dtoList, HttpStatus.OK);
    }

    @ApiOperation("특정 토픽별 모든 보기 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 토픽별 보기 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽별 보기 조회 요청")
    })
    @GetMapping("/topics/{topicTitle}/descriptions")
    public ResponseEntity<List<DescriptionDto>> getDescriptionsInTopic(@PathVariable String topicTitle){
        List<DescriptionDto> dtoList = topicSimpleQueryService.queryTopicDescriptions(topicTitle);

        return new ResponseEntity<List<DescriptionDto>>(dtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 토픽별 모든 선지 조회")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회")
    })
    @GetMapping("/admin/topics/{topicTitle}/choices/")
    public ResponseEntity<List<ChoiceDto>> getChoicesInTopics(@PathVariable("topicTitle") String topicTitle){
        List<ChoiceDto> dtoList = topicSimpleQueryService.queryTopicChoices(topicTitle);
        return new ResponseEntity<List<ChoiceDto>>(dtoList,HttpStatus.OK);
    }


    @ApiOperation(value = "새로운 상세정보 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "상세정보 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 상세정보 생성 실패"),
    })
    @PostMapping("/admin/topics")
    public ResponseEntity<Void> createTopic(@Validated @RequestBody TopicDetailDto topicDetailDto) {

        topicService.createTopic(topicDetailDto);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상세정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상세정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인해 상세정보 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상세정보 수정 시도")
    })
    @PatchMapping("/admin/topics/{topicTitle}")
    public ResponseEntity<Void> updateTopic(@PathVariable("topicTitle")String topicTitle,@Validated @RequestBody TopicDetailDto topicDetailDto) {

        topicService.updateTopic(topicTitle, topicDetailDto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(value = "주제 순서번호 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주제 순서번호 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주제 제목 입력")
    })
    @PatchMapping("/admin/topic-numbers")
    public ResponseEntity<Void> updateTopicNumber(@Validated @RequestBody List<TopicNumberDto> topicNumberDtoList) {
        topicService.updateTopicNumber(topicNumberDtoList);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(value = "상세정보 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 삭제"),
            @ApiResponse(responseCode = "400", description = "해당 토픽에 선지/보기가 존재하는 경우"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상세정보 삭제 요청")
    })
    @DeleteMapping("/admin/topics/{topicTitle}")
    public ResponseEntity<Void> deleteTopic(@PathVariable("topicTitle") String topicTitle) {
        topicService.deleteTopic(topicTitle);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
