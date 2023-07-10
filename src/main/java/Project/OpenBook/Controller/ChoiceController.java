package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Choice;
import Project.OpenBook.Dto.choice.ChoiceAddDto;
import Project.OpenBook.Dto.choice.ChoiceDto;
import Project.OpenBook.Dto.choice.ChoiceUpdateDto;
import Project.OpenBook.Service.ChoiceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        List<ChoiceDto> choiceList = choiceService.queryChoicesByTopic(topicTitle);
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

        return new ResponseEntity(choiceDto, HttpStatus.OK);
    }

//    @ApiOperation(value = "주어진 선지와 같은 주제의 선지 보여주기", notes = "문제 생성/수정시 선지 새로고침을 위한 endPoint")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공적인 조회"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 선지 아이디 요청")
//    })
//    @GetMapping("/admin/choices/random/{choiceId}")
//    public ResponseEntity getRandomChoiceSameTopic(@PathVariable("choiceId") Long choiceId){
//        ChoiceDto choiceDto = choiceService.queryRandomChoice(choiceId);
//
//        return new ResponseEntity(choiceDto, HttpStatus.OK);
//    }

    @ApiOperation(value = "선지를 여러개 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공적인 선지 추가"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 선지 추가 실패")
    })
    @PostMapping("/admin/choices/")
    public ResponseEntity addChoices(@Validated @RequestBody ChoiceAddDto choiceAddDto){
        choiceService.addChoices(choiceAddDto);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "선지 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 선지 수정"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 선지 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 선지 수정 요청")
    })
    @PatchMapping("/admin/choices/{choiceId}")
    public ResponseEntity updateChoices(@PathVariable Long choiceId,@Validated @RequestBody ChoiceUpdateDto choiceUpdateDto) {

        Choice choice = choiceService.updateChoice(choiceUpdateDto, choiceId);

        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "선지 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 삭제"),
            @ApiResponse(responseCode = "404", description = "존재하지 않은 선지 삭제 시도")
    })
    @DeleteMapping("/admin/choices/{choiceId}")
    public ResponseEntity deleteChoices(@PathVariable Long choiceId) {
        choiceService.deleteChoice(choiceId);

        return new ResponseEntity(HttpStatus.OK);
    }

}

