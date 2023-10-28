package Project.OpenBook.Domain.Question.Controller;

import Project.OpenBook.Domain.Question.Service.QuestionService;
import Project.OpenBook.Domain.Question.Dto.QuestionDto;
import Project.OpenBook.Domain.Question.Dto.TimeFlowQuestionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class QuestionController {

    private final QuestionService questionService;

    @Operation(summary = "timeline 내 연표 문제 제공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "연표문제 제공 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/questions/time-flow")
    public ResponseEntity<List<TimeFlowQuestionDto>> queryTimeFlowQuestion(@RequestParam("id") Long id) {
        List<TimeFlowQuestionDto> timeFlowQuestionDtoList = questionService.queryTimeFlowQuestion(id);

        return new ResponseEntity<List<TimeFlowQuestionDto>>(timeFlowQuestionDtoList, HttpStatus.OK);
    }

    @Operation(summary = "주제보고 키워드 맞추기 문제 제공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주제보고 문장 맞추기 문제 제공 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주제 이름 입력")
    })
    @GetMapping("/questions/get-keywords")
    public ResponseEntity<List<QuestionDto>> queryGetKeywordsQuestion(@RequestParam("title") String topicTitle) {
        List<QuestionDto> questionDtoList = questionService.queryGetKeywordsQuestion(topicTitle);

        return new ResponseEntity<List<QuestionDto>>(questionDtoList, HttpStatus.OK);
    }


    @Operation(summary = "키워드 보고 주제 맞추기 문제 제공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드 보고 주제 맞추기 문제 제공 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 주제 이름 입력")
    })
    @GetMapping("/questions/get-topics-keywords")
    public ResponseEntity<List<QuestionDto>> queryGetTopicsByKeywordQuestion(@RequestParam("num") Integer num){
        List<QuestionDto> questionDtoList = questionService.queryGetTopicsByKeywordQuestion(num);

        return new ResponseEntity<List<QuestionDto>>(questionDtoList, HttpStatus.OK);
    }



    @Operation(summary = "랜덤 문제 제공")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "문제 제공 성공"),
            @ApiResponse(responseCode = "400", description = "문제 제공 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/questions/random")
    public ResponseEntity<List<QuestionDto>> queryRandomQuestion(@RequestParam("num") Integer chapterNum, @RequestParam("count") Integer questionCount) {
        List<QuestionDto> questionDtoList = questionService.queryRandomQuestion(chapterNum, questionCount);
        return new ResponseEntity<List<QuestionDto>>(questionDtoList, HttpStatus.OK);
    }

}
