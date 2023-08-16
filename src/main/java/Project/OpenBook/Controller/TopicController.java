package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Domain.Description;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.Sentence.SentenceDto;
import Project.OpenBook.Dto.choice.ChoiceDto;
import Project.OpenBook.Dto.description.DescriptionDto;
import Project.OpenBook.Dto.keyword.KeywordDto;
import Project.OpenBook.Dto.topic.TopicAdminDto;
import Project.OpenBook.Dto.topic.TopicCustomerDto;
import Project.OpenBook.Dto.topic.TopicNumberDto;
import Project.OpenBook.Service.ChoiceService;
import Project.OpenBook.Service.DescriptionService;
import Project.OpenBook.Service.StudyProgressService;
import Project.OpenBook.Service.TopicService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Currency;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
public class TopicController {

    private final TopicService topicService;
    private final DescriptionService descriptionService;
    private final ChoiceService choiceService;

    @ApiOperation(value = "각 토픽에 대한 상세정보 조회 - 관리자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 상세정보 조회 성공")
    })
    @GetMapping("admin/topics/{topicTitle}")
    public ResponseEntity queryTopicsAdmin( @PathVariable("topicTitle") String topicTitle) {
        TopicAdminDto topicAdminDto = topicService.queryTopicAdmin(topicTitle);

        return new ResponseEntity(topicAdminDto, HttpStatus.OK);
    }

    @ApiOperation(value = "각 토픽에 대한 상세정보 조회 - 사용자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 상세정보 조회 성공")
    })
    @GetMapping("/topics/{topicTitle}")
    public ResponseEntity queryTopicsUser(@PathVariable("topicTitle") String topicTitle) {
        TopicCustomerDto topicCustomerDto = topicService.queryTopicCustomer(topicTitle);

        return new ResponseEntity(topicCustomerDto, HttpStatus.OK);
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

    @ApiOperation("특정 토픽별 모든 보기 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 토픽별 보기 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽별 보기 조회 요청")
    })
    @GetMapping("/topics/{topicTitle}/descriptions")
    public ResponseEntity getDescriptionsInTopic(@PathVariable String topicTitle){
        List<Description> descriptionList = descriptionService.queryDescriptionsInTopic(topicTitle);

        List<DescriptionDto> descriptionDtoList = descriptionList.stream().map(d -> new DescriptionDto(d)).collect(Collectors.toList());
        return new ResponseEntity(descriptionDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "특정 토픽별 모든 선지 조회")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회")
    })
    @GetMapping("/admin/topics/{topicTitle}/choices/")
    public ResponseEntity getChoicesInTopics(@PathVariable("topicTitle") String topicTitle){
        List<ChoiceDto> choiceList = choiceService.queryChoicesByTopic(topicTitle);
        return new ResponseEntity(choiceList,HttpStatus.OK);
    }


    @ApiOperation(value = "새로운 상세정보 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "상세정보 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 상세정보 생성 실패"),
    })
    @PostMapping("/admin/topics")
    public ResponseEntity createTopic(@Validated @RequestBody TopicAdminDto topicAdminDto) {

        Topic topic = topicService.createTopic(topicAdminDto);
        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상세정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상세정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인해 상세정보 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상세정보 수정 시도")
    })
    @PatchMapping("/admin/topics/{topicTitle}")
    public ResponseEntity updateTopic(@PathVariable("topicTitle")String topicTitle,@Validated @RequestBody TopicAdminDto topicAdminDto) {

        Topic topic = topicService.updateTopic(topicTitle, topicAdminDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "주제 순서번호 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주제 순서번호 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주제 제목 입력")
    })
    @PatchMapping("/admin/topic-numbers")
    public ResponseEntity updateTopicNumber(@Validated @RequestBody List<TopicNumberDto> topicNumberDtoList) {
        topicService.updateTopicNumber(topicNumberDtoList);
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
