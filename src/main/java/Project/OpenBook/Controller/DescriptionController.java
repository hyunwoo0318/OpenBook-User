package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Description;
import Project.OpenBook.Dto.description.DescriptionCreateDto;
import Project.OpenBook.Dto.description.DescriptionDto;
import Project.OpenBook.Dto.description.DescriptionUpdateDto;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Service.DescriptionService;
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
public class DescriptionController {

    private final DescriptionService descriptionService;

    @ApiOperation("특정 보기 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 보기 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 보기 조회 요청")
    })
    @GetMapping("/admin/descriptions/{descriptionId}")
    public ResponseEntity queryDescription(@PathVariable Long descriptionId) {
        DescriptionDto descriptionDto = descriptionService.queryDescription(descriptionId);
        if (descriptionDto == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(descriptionDto, HttpStatus.OK);
    }

    @ApiOperation("특정 토픽별 모든 보기 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 토픽별 보기 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 토픽별 보기 조회 요청")
    })
    @GetMapping("/topics/{topicTitle}/descriptions")
    public ResponseEntity getDescriptionsInTopic(@PathVariable String topicTitle){
        List<Description> descriptionList = descriptionService.queryDescriptionsInTopic(topicTitle);
        if (descriptionList == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        List<DescriptionDto> descriptionDtoList = descriptionList.stream().map(d -> new DescriptionDto(d)).collect(Collectors.toList());
        return new ResponseEntity(descriptionDtoList, HttpStatus.OK);
    }


    @ApiOperation("보기 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "201", description = "성공적인 보기 생성"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 보기 생성 실패"),
            @ApiResponse(responseCode =  "404", description = "존재하지 않는 topicTitle 입력")
    })
    @PostMapping("/admin/descriptions/")
    public ResponseEntity addDescription(@Validated @RequestBody DescriptionCreateDto descriptionCreateDto, BindingResult bindingResult){
        List<ErrorDto> errorDtoList = new ArrayList<>();
        List<Description> descriptionList = descriptionService.addDescription(descriptionCreateDto);
        if (descriptionList == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        List<Long> descriptionIdList = descriptionList.stream().map(d -> d.getId()).collect(Collectors.toList());
        return new ResponseEntity(descriptionIdList, HttpStatus.CREATED);
    }

    @ApiOperation("보기 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode =  "201", description = "성공적인 보기 수정"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 보기 수정 실패"),
            @ApiResponse(responseCode =  "404", description = "존재하지 않는 topicTitle이나 보기 id 입력으로 인한 수정 실패")
    })
    @PatchMapping("/admin/descriptions/{descriptionId}")
    public ResponseEntity addDescription(@PathVariable Long descriptionId, @Validated @RequestBody DescriptionUpdateDto descriptionUpdateDto, BindingResult bindingResult){
        List<ErrorDto> errorDtoList = new ArrayList<>();
        Description description = descriptionService.updateDescription(descriptionId, descriptionUpdateDto);
        if (description == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        if (bindingResult.hasErrors()) {
            errorDtoList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
            return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(description.getId(), HttpStatus.OK);
    }

    @ApiOperation("보기 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "성공적인 보기 삭제"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 topicTitle이나 보기 id 입력으로 인한 삭제 실패")
    })
    @DeleteMapping("/admin/descriptions/{descriptionId}")
    public ResponseEntity deleteDescription(@PathVariable Long descriptionId) {
        boolean res = descriptionService.deleteDescription(descriptionId);
        if (!res) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.OK);
    }


}
