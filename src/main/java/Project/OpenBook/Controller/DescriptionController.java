package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Description;
import Project.OpenBook.Dto.choice.DupChoiceDto;
import Project.OpenBook.Dto.description.DescriptionCreateDto;
import Project.OpenBook.Dto.description.DescriptionDto;
import Project.OpenBook.Dto.description.DescriptionUpdateDto;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Service.DescriptionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class DescriptionController {

    private final DescriptionService descriptionService;

    @ApiOperation("특정 보기 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 보기 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 보기 조회 요청")
    })
    @GetMapping("/admin/descriptions/{descriptionId}")
    public ResponseEntity<DescriptionDto> queryDescription(@PathVariable Long descriptionId) {
        DescriptionDto descriptionDto = descriptionService.queryDescription(descriptionId);

        return new ResponseEntity<DescriptionDto>(descriptionDto, HttpStatus.OK);
    }


    @ApiOperation(value = "특정 토픽의 선지들을 조회", notes = "각각의 선지가 특정 보기와 내용이 겹친다고 선정되었는지에 대한 data return")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 보기id, topic제목 입력")
    })
    @GetMapping("/admin/descriptions/{descriptionId}/topics/{topicTitle}/choices")
    public ResponseEntity<List<DupChoiceDto>> queryTopicDupChoices(@PathVariable("descriptionId") Long descriptionId,@PathVariable("topicTitle") String topicTitle){
        List<DupChoiceDto> dupChoiceDtoList = descriptionService.queryTopicDupChoices(descriptionId, topicTitle);
        return new ResponseEntity<List<DupChoiceDto>>(dupChoiceDtoList, HttpStatus.OK);
    }

//    @ApiOperation(value = "주어진 보기와 같은 토픽의 보기 조회", notes = "문제 생성/수정시 보기 새로고침을 위한 endPoint")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "성공적인 보기 조회"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 보기 아이디 제공")
//    })
//    @GetMapping("/admin/descriptions/random/{descriptionId}")
//    public ResponseEntity getDescriptionInSameTopic(@PathVariable("descriptionId") Long descriptionId){
//        DescriptionDto descriptionDto = descriptionService.queryRandomDescription(descriptionId);
//
//        return new ResponseEntity(descriptionDto, HttpStatus.OK);
//    }


    @ApiOperation("보기 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "201", description = "성공적인 보기 생성"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 보기 생성 실패"),
            @ApiResponse(responseCode =  "404", description = "존재하지 않는 topicTitle 입력")
    })
    @PostMapping("/admin/descriptions/")
    public ResponseEntity<Void> addDescription(@Validated @RequestBody DescriptionCreateDto descriptionCreateDto){

        List<Description> descriptionList = descriptionService.addDescription(descriptionCreateDto);

        List<Long> descriptionIdList = descriptionList.stream().map(d -> d.getId()).collect(Collectors.toList());
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @ApiOperation("보기 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "201", description = "성공적인 보기 수정"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 보기 수정 실패"),
            @ApiResponse(responseCode =  "404", description = "존재하지 않는 topicTitle이나 보기 id 입력으로 인한 수정 실패")
    })
    @PatchMapping("/admin/descriptions/{descriptionId}")
    public ResponseEntity<Void> addDescription(@PathVariable Long descriptionId, @Validated @RequestBody DescriptionUpdateDto descriptionUpdateDto){

        Description description = descriptionService.updateDescription(descriptionId, descriptionUpdateDto);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation("보기 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공적인 보기 삭제"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 topicTitle이나 보기 id 입력으로 인한 삭제 실패")
    })
    @DeleteMapping("/admin/descriptions/{descriptionId}")
    public ResponseEntity<Void> deleteDescription(@PathVariable Long descriptionId) {
        descriptionService.deleteDescription(descriptionId);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


}
