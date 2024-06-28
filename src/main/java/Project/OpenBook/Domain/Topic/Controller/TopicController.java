package Project.OpenBook.Domain.Topic.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordDto;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Domain.Topic.Service.TopicService;
import Project.OpenBook.Domain.Topic.Service.dto.BookmarkedTopicQueryDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicListQueryDto;
import Project.OpenBook.Domain.Topic.Service.dto.TopicWithKeywordDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@RestController
public class TopicController {

    private final TopicService topicService;
    private final TopicRepository topicRepository;

    @Operation(summary = "각 토픽에 대한 상세정보 조회 - 사용자")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "토픽 상세정보 조회 성공")})
    @GetMapping("/topics/{topicTitle}")
    @Transactional
    public ResponseEntity<TopicWithKeywordDto> queryTopicsTitle(
            @PathVariable("topicTitle") String topicTitle) {
        TopicWithKeywordDto dto = topicService.queryTopicsCustomer(topicTitle);

        return new ResponseEntity<TopicWithKeywordDto>(dto, HttpStatus.OK);
    }

    @Operation(summary = "해당 단원의 모든 topic 조회 - 사용자")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "전체 topic 조회 성공"),
                @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력"),
            })
    @GetMapping("/chapters/{num}/topics")
    public ResponseEntity<List<TopicListQueryDto>> queryChapterTopicsCustomer(
            @Parameter(hidden = true) @AuthenticationPrincipal Customer customer,
            @PathVariable("num") int num) {
        List<TopicListQueryDto> dtoList = new ArrayList<>();
        if (customer == null) {
            dtoList = topicService.queryChapterTopicsForFree(num);
        } else {
            dtoList = topicService.queryChapterTopicsCustomer(customer, num);
        }

        return new ResponseEntity<List<TopicListQueryDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "특정 question-category 내의 모든 topic조회")
    @GetMapping("/question-categories/{id}/topics")
    public ResponseEntity<List<BookmarkedTopicQueryDto>> queryTopicsInQuestionCategory(
            @Parameter(hidden = true) @AuthenticationPrincipal Customer customer,
            @PathVariable("id") Long id) {
        if (customer == null) {
            List<BookmarkedTopicQueryDto> dtoList =
                    topicService.queryTopicsInQuestionCategory(customer, id);
            return new ResponseEntity<List<BookmarkedTopicQueryDto>>(dtoList, HttpStatus.OK);
        } else {
            List<BookmarkedTopicQueryDto> dtoList =
                    topicService.queryTopicsInQuestionCategoryNotLogin(id);
            return new ResponseEntity<List<BookmarkedTopicQueryDto>>(dtoList, HttpStatus.OK);
        }
    }

    @Operation(summary = "특정 토픽의 전체 키워드 조회")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "특정 토픽의 전체 키워드 조회 성공"),
                @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽 제목 입력")
            })
    @GetMapping("/topics/{topicTitle}/keywords")
    public ResponseEntity<List<KeywordDto>> queryTopicKeywords(
            @PathVariable("topicTitle") String topicTitle) {
        List<KeywordDto> dtoList = topicService.queryTopicKeywords(topicTitle);

        return new ResponseEntity<List<KeywordDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "북마크한 토픽 전체 조회")
    @ApiResponses(
            value = {
                @ApiResponse(responseCode = "200", description = "특정 토픽의 전체 키워드 조회 성공"),
                @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽 제목 입력")
            })
    @GetMapping("/topics/bookmarked")
    public ResponseEntity<List<BookmarkedTopicQueryDto>> queryBookmarkedTopics(
            @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true)
                    Customer customer) {
        List<BookmarkedTopicQueryDto> dtoList = topicService.queryBookmarkedTopics(customer);

        return new ResponseEntity<List<BookmarkedTopicQueryDto>>(dtoList, HttpStatus.OK);
    }
}
