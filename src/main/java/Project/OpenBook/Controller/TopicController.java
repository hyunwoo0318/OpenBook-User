package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.topic.TopicDto;
import Project.OpenBook.Service.TopicService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/topics")
public class TopicController {

    private final TopicService topicService;

    @ApiOperation(value = "새로운 상세정보 입력")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "상세정보 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 상세정보 생성 실패"),
    })
    @PostMapping
    public ResponseEntity createTopic(@Validated @RequestBody TopicDto topicDto, BindingResult bindingResult) {
        List<ErrorDto> errorDtoList = new ArrayList<>();

        if (bindingResult.hasErrors()) {
           errorDtoList  = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
           return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        Topic topic = topicService.createTopic(topicDto, errorDtoList);
        if (topic == null) {
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "상세정보 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "상세정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인해 상세정보 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상세정보 수정 시도")
    })
    @PatchMapping("/{topicTitle}")
    public ResponseEntity updateTopic(@PathVariable("topicTitle")String topicTitle,@RequestBody TopicDto topicDto, BindingResult bindingResult) {
        System.out.println(topicDto.toString());
        List<ErrorDto> errorDtoList = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            List<ObjectError> allErrors = bindingResult.getAllErrors();
            for (ObjectError allError : allErrors) {
                System.out.println(allError.getObjectName() + " " + allError.getDefaultMessage());
            }
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        Topic topic = topicService.updateTopic(topicTitle, topicDto, errorDtoList);
        if (topic == null && errorDtoList.isEmpty()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (!errorDtoList.isEmpty()) {
            for (ErrorDto errorDto : errorDtoList) {
                System.out.println(errorDto.getField() + " " + errorDto.getMessage() );
            }
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "상세정보 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 삭제"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 상세정보 삭제 요청")
    })
    @DeleteMapping("/{topicTitle}")
    public ResponseEntity deleteTopic(@PathVariable("topicTitle") String topicTitle) {
        if(!topicService.deleteTopic(topicTitle)){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }
}
