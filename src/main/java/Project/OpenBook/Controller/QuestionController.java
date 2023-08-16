package Project.OpenBook.Controller;

import Project.OpenBook.Constants.ProgressConst;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Domain.Question;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.question.*;
import Project.OpenBook.Service.CategoryService;
import Project.OpenBook.Service.CustomerService;
import Project.OpenBook.Service.QuestionService;
import Project.OpenBook.Service.StudyProgressService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @ApiOperation("단원 내 연표 문제 제공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "연표문제 제공 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/questions/time-flow")
    public ResponseEntity queryTimeFlowQuestion(@RequestParam("num") Integer num) {
        List<TimeFlowQuestionDto> timeFlowQuestionDtoList = questionService.queryTimeFlowQuestion(num);

        return new ResponseEntity(timeFlowQuestionDtoList, HttpStatus.OK);
    }

    @ApiOperation("주제보고 키워드 맞추기 문제 제공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주제보고 문장 맞추기 문제 제공 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주제 이름 입력")
    })
    @GetMapping("/questions/get-keywords")
    public ResponseEntity queryGetKeywordsQuestion(@RequestParam("title") String topicTitle) {
        List<QuestionDto> questionDtoList = questionService.queryGetKeywordsQuestion(topicTitle);

        return new ResponseEntity(questionDtoList, HttpStatus.OK);
    }

    @ApiOperation("주제보고 문장 맞추기 문제 제공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주제보고 문장 맞추기 문제 제공 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주제 이름 입력")
    })
    @GetMapping("/questions/get-sentences")
    public ResponseEntity queryGetSentencesQuestion(@RequestParam("title") String topicTitle) {
        List<QuestionDto> questionDtoList = questionService.queryGetSentencesQuestion(topicTitle);

        return new ResponseEntity(questionDtoList, HttpStatus.OK);
    }

    @ApiOperation("키워드 보고 주제 맞추기 문제 제공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드 보고 주제 맞추기 문제 제공 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주제 이름 입력")
    })
    @GetMapping("/questions/get-topics-keywords")
    public ResponseEntity queryGetTopicsByKeywordQuestion(@RequestParam("num") Integer num){
        List<QuestionDto> questionDtoList = questionService.queryGetTopicsByKeywordQuestion(num);

        return new ResponseEntity(questionDtoList, HttpStatus.OK);
    }

    @ApiOperation("문장 보고 주제 맞추기 문제 제공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드/문장 보고 주제 맞추기 문제 제공 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주제 이름 입력")
    })
    @GetMapping("/questions/get-topics-sentences")
    public ResponseEntity queryGetTopicsBySentenceQuestion(@RequestParam("num") Integer num){
        List<QuestionDto> questionDtoList = questionService.queryGetTopicsBySentenceQuestion(num);

        return new ResponseEntity(questionDtoList, HttpStatus.OK);
    }
}
