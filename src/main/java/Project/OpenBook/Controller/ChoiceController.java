package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Dto.ChoiceAddDto;
import Project.OpenBook.Dto.ChoiceDto;
import Project.OpenBook.Dto.ChoiceUpdateDto;
import Project.OpenBook.Dto.ErrorDto;
import Project.OpenBook.Service.ChoiceService;
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
public class ChoiceController {

    private final ChoiceService choiceService;

    @ApiOperation(value = "특정 topic의 모든 선지를 보여줌")
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회")
    })
    @GetMapping("/admin/topics/{topicTitle}/choices/")
    public ResponseEntity getChoicesInTopics(@PathVariable("topicTitle") String topicTitle){
        List<ChoiceDto> choiceList = choiceService.queryTopicsByTopic(topicTitle);
        return new ResponseEntity(choiceList,HttpStatus.OK);
    }

    @ApiOperation(value = "특정 선지를 보여줌")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 선지 조회 요청")
    })
    @GetMapping("/admin/choices/{choiceId}")
    public ResponseEntity getChoice(@PathVariable("choiceId") Long choiceId) {
        ChoiceDto choiceDto = choiceService.queryChoice(choiceId);
        if (choiceDto == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(choiceDto, HttpStatus.OK);
    }

    @ApiOperation(value = "선지를 여러개 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공적인 선지 추가"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 선지 추가 실패")
    })
    @PostMapping("/admin/choices/")
    public ResponseEntity addChoices(@Validated @RequestBody ChoiceAddDto choiceAddDto, BindingResult bindingResult){
        List<ErrorDto> errorDtoList = new ArrayList<>();
        Boolean res = choiceService.addChoices(choiceAddDto);

        if (bindingResult.hasErrors()) {
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
        }
        if (!res) {
            errorDtoList.add(new ErrorDto("topicTitle", "정확한 토픽제목을 입력해주세요"));
        }
        if(!errorDtoList.isEmpty()){
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "선지 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 선지 수정"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 선지 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 선지 수정 요청")
    })
    @PatchMapping("/admin/choices/{choiceId}")
    public ResponseEntity updateChoices(@PathVariable Long choiceId,@Validated @RequestBody ChoiceUpdateDto choiceUpdateDto, BindingResult bindingResult) {
        List<ErrorDto> errorDtoList = new ArrayList<>();
        Choice choice = choiceService.updateChoice(choiceUpdateDto, choiceId);

        if (bindingResult.hasErrors()) {
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
        }
        if (choice == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if(!errorDtoList.isEmpty()){
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "여러 선지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 삭제"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 선지 삭제 시도")
    })
    @DeleteMapping("/admin/choices/{choiceId}")
    public ResponseEntity deleteChoices(@PathVariable Long choiceId) {
        List<ErrorDto> errorDtoList = new ArrayList<>();
        Boolean res = choiceService.deleteChoice(choiceId);
        if(!res){
            errorDtoList.add(new ErrorDto("choiceId", "존재하지 않는 선지 id를 입력했습니다."));
            return new ResponseEntity(errorDtoList, HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

//    @ApiOperation(value = "여러개의 선지 수정")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공적인 선지 수정"),
//            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 선지 수정 실패"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 선지 수정 요청")
//    })
//    @PatchMapping("/admin/choices/")
//    public ResponseEntity updateChoices(@Validated @RequestBody ChoiceUpdateDto choiceUpdateDto, BindingResult bindingResult) {
//        List<ErrorDto> errorDtoList = new ArrayList<>();
//        Boolean res = choiceService.updateChoices(choiceUpdateDto);
//
//        if (bindingResult.hasErrors()) {
//            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
//        }
//        if (!res) {
//            return new ResponseEntity(HttpStatus.NOT_FOUND);
//        }
//
//        if(!errorDtoList.isEmpty()){
//            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
//        }
//
//        return new ResponseEntity(HttpStatus.OK);
//    }
//
//    @ApiOperation(value = "여러 선지 삭제")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공적인 삭제"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않은 선지 삭제 시도")
//    })
//    @DeleteMapping("/admin/choices/")
//    public ResponseEntity deleteChoices(@RequestBody List<Long> choiceIdList) {
//        List<ErrorDto> errorDtoList = new ArrayList<>();
//        Boolean res = choiceService.deleteChoices(choiceIdList);
//        if(!res){
//            errorDtoList.add(new ErrorDto("choiceId", "존재하지 않는 선지 id를 입력했습니다."));
//            return new ResponseEntity(errorDtoList, HttpStatus.NOT_FOUND);
//        }
//
//        return new ResponseEntity(HttpStatus.OK);
//    }
}
