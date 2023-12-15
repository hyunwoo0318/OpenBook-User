package Project.OpenBook.Domain.Topic.Controller;

import Project.OpenBook.Domain.Chapter.Service.dto.ChapterTopicWithCountDto;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordDto;
import Project.OpenBook.Domain.Topic.Service.TopicService;
import Project.OpenBook.Domain.Topic.Service.TopicSimpleQueryService;
import Project.OpenBook.Domain.Topic.Service.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class TopicController {

    private final TopicService topicService;
    private final TopicSimpleQueryService topicSimpleQueryService;

    @Operation(summary = "각 토픽에 대한 상세정보 조회 - 관리자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 상세정보 조회 성공")
    })
    @GetMapping("/admin/topics/{topicTitle}")
    public ResponseEntity<TopicDetailDto> queryTopicsAdmin(@PathVariable("topicTitle") String topicTitle) {
        TopicDetailDto dto = topicSimpleQueryService.queryTopicsAdmin(topicTitle);

        return new ResponseEntity<TopicDetailDto>(dto, HttpStatus.OK);
    }

    @Operation(summary = "각 토픽에 대한 상세정보 조회 - 사용자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토픽 상세정보 조회 성공")
    })
    @GetMapping("/topics/{topicTitle}")
    @Transactional
    public ResponseEntity<TopicWithKeywordDto> queryTopicsTitle(@PathVariable("topicTitle") String topicTitle) {
        TopicWithKeywordDto dto = topicSimpleQueryService.queryTopicsCustomer(topicTitle);

        return new ResponseEntity<TopicWithKeywordDto>(dto, HttpStatus.OK);
    }

    @Operation(summary = "해당 단원의 모든 topic 조회 - 관리자")
    @GetMapping("/admin/chapters/{num}/topics")
    public ResponseEntity<List<ChapterTopicWithCountDto>> queryChapterTopicsAdmin(@PathVariable("num") int num) {
        List<ChapterTopicWithCountDto> dtoList = topicSimpleQueryService.queryChapterTopicsAdmin(num);

        return new ResponseEntity<List<ChapterTopicWithCountDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "해당 단원의 모든 topic 조회 - 사용자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 topic 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력"),
    })
    @GetMapping("/chapters/{num}/topics")
    public ResponseEntity<List<TopicListQueryDto>> queryChapterTopicsCustomer(@Parameter(hidden = true) @AuthenticationPrincipal Customer customer,
                                                                                @PathVariable("num") int num){
        List<TopicListQueryDto> dtoList = new ArrayList<>();
        if (customer == null) {
            dtoList = topicSimpleQueryService.queryChapterTopicsForFree(num);
        }else{
            dtoList = topicSimpleQueryService.queryChapterTopicsCustomer(customer,num);
        }

        return new ResponseEntity<List<TopicListQueryDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "특정 question-category 내의 모든 topic조회")
    @GetMapping("/question-categories/{id}/topics")
    public ResponseEntity<List<BookmarkedTopicQueryDto>> queryTopicsInQuestionCategory(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
                                                                                               @PathVariable("id") Long id) {
        List<BookmarkedTopicQueryDto> dtoList = topicSimpleQueryService.queryTopicsInQuestionCategory(customer ,id);
        return new ResponseEntity<List<BookmarkedTopicQueryDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "특정 토픽의 전체 키워드 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 토픽의 전체 키워드 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽 제목 입력")
    })
    @GetMapping("/topics/{topicTitle}/keywords")
    public ResponseEntity<List<KeywordDto>> queryTopicKeywords(@PathVariable("topicTitle") String topicTitle) {
        List<KeywordDto> dtoList = topicSimpleQueryService.queryTopicKeywords(topicTitle);

        return new ResponseEntity<List<KeywordDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "북마크한 토픽 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 토픽의 전체 키워드 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽 제목 입력")
    })
    @GetMapping("/topics/bookmarked")
    public ResponseEntity<List<BookmarkedTopicQueryDto>> queryBookmarkedTopics(@Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer) {
        List<BookmarkedTopicQueryDto> dtoList = topicSimpleQueryService.queryBookmarkedTopics(customer);

        return new ResponseEntity<List<BookmarkedTopicQueryDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "새로운 상세정보 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "상세정보 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 상세정보 생성 실패"),
    })
    @PostMapping("/admin/topics")
    public ResponseEntity<Void> createTopic(@Validated @RequestBody TopicAddUpdateDto topicAddUpdateDto) {

        topicService.createTopic(topicAddUpdateDto);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @Operation(summary = "상세정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상세정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인해 상세정보 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상세정보 수정 시도")
    })
    @PatchMapping("/admin/topics/{topicTitle}")
    public ResponseEntity<Void> updateTopic(@PathVariable("topicTitle")String topicTitle
            ,@Validated @RequestBody TopicAddUpdateDto topicAddUpdateDto) {

        topicService.updateTopic(topicTitle, topicAddUpdateDto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "주제 순서번호 수정")
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

    @Operation(summary = "상세정보 삭제")
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
