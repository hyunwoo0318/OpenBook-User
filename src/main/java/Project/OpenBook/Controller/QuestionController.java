package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Question;
import Project.OpenBook.Dto.ErrorDto;
import Project.OpenBook.Dto.QuestionDto;
import Project.OpenBook.Service.CategoryService;
import Project.OpenBook.Service.QuestionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private final CategoryService categoryService;

    @ApiOperation("문제를 임의로 생성해 보여줌")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 문제 생성"),
            @ApiResponse(responseCode = "404", description = "존재하지 카테고리나 문제 타입 입력")
    })
    @GetMapping("/admin/temp-question")
    public ResponseEntity makeTempQuestion(@RequestParam("category") String categoryName, @RequestParam("type") Long type,
                                           @RequestParam(value = "topic", required = false) String topicTitle){
        if(!categoryService.findCategory(categoryName)){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        QuestionDto questionDto = questionService.makeTempQuestion(type, categoryName,topicTitle);
        return new ResponseEntity(questionDto, HttpStatus.OK);
    }

    @ApiOperation("문제 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 문제 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 문제 조회 요청")
    })
    @GetMapping("/questions/{id}")
    public ResponseEntity queryQuestion(@PathVariable Long id) {
        QuestionDto questionDto = questionService.queryQuestion(id);
        if (questionDto == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(questionDto, HttpStatus.OK);
    }

    @ApiOperation("문제 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공적인 문제 생성"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 문제 생성 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리나 문제 타입 입력으로 인한 문제 생성 실패")
    })
    @PostMapping("/admin/questions")
    public ResponseEntity addQuestion(@Validated @RequestBody QuestionDto questionDto, BindingResult bindingResult) {

        List<ErrorDto> errorDtoList = new ArrayList<>();
        Question question = questionService.addQuestion(questionDto);
        if (question == null) {
            errorDtoList.add(new ErrorDto("question", "문제 생성 실패!"));
        }
        if (bindingResult.hasErrors()) {
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
        }

        if(!errorDtoList.isEmpty()){
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        if (question == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(question.getId(), HttpStatus.CREATED);
    }

    @ApiOperation("문제 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 문제 수정"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 문제 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리, 문제 타입, 문제 아이디 입력으로 인한 문제 수정 실패")
    })
    @PatchMapping("/admin/questions/{questionId}")
    public ResponseEntity updateQuestion(@PathVariable Long questionId,@Validated @RequestBody QuestionDto questionDto, BindingResult bindingResult) {

        List<ErrorDto> errorDtoList = new ArrayList<>();
        Question question = questionService.updateQuestion(questionId, questionDto);

        if (question == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(question.getId(),HttpStatus.OK);
    }

    @ApiOperation("문제 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 문제 삭제"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 문제 아이디 입력으로 인한 문제 수정 실패")
    })
    @DeleteMapping("/admin/questions/{questionId}")
    public ResponseEntity deleteQuestion(@PathVariable("question-id") Long questionId) {

        boolean res = questionService.deleteQuestion(questionId);
        if(!res){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.OK);
    }


}
