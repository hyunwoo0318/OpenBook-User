package Project.OpenBook.Domain.Keyword.Controller;

import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordCreateDto;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordNumberDto;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordUserDto;
import Project.OpenBook.Domain.Keyword.Service.Dto.KeywordWithTopicDto;
import Project.OpenBook.Domain.Keyword.Service.KeywordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "키워드 전체 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드 전체 조회 성공"),
    })
    @GetMapping("/admin/keywords")
    public ResponseEntity<List<KeywordWithTopicDto>> queryTotalKeywords() {
        List<KeywordWithTopicDto> dtoList
                = keywordService.queryTotalKeywords();
        return new ResponseEntity<>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "키워드 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "키워드 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 키워드 생성 실패"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 키워드 이름 입력")
    })
    @PostMapping("admin/keywords")
    public ResponseEntity<Void> createService(@Validated @RequestBody KeywordCreateDto keywordCreateDto) throws IOException {
        keywordService.createKeyword(keywordCreateDto);

        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }


    @Operation(summary = "키워드 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인하여 키워드 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 키워드 수정 시도"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 키워드 이름 입력")
    })
    @PatchMapping("admin/keywords/{keywordId}")
    public ResponseEntity<Void> updateKeyword(@PathVariable("keywordId") Long keywordId,
                                        @Validated @RequestBody KeywordUserDto keywordUserDto) throws IOException {
        keywordService.updateKeyword(keywordId, keywordUserDto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "키워드 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재히지 않는 키워드 삭제 시도")
    })
    @DeleteMapping("admin/keywords/{keywordId}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable("keywordId") Long keywordId) {
        keywordService.deleteKeyword(keywordId);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "키워드 번호 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인하여 키워드 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 키워드 수정 시도")
    })
    @PatchMapping("admin/keyword-numbers")
    public ResponseEntity<Void> updateKeywordNumbers(@Validated @RequestBody List<KeywordNumberDto> keywordNumberDtoList) throws IOException {
        keywordService.updateKeywordNumbers(keywordNumberDtoList);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


}

