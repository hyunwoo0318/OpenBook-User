package Project.OpenBook.Controller;

import Project.OpenBook.Dto.keyword.KeywordCreateDto;
import Project.OpenBook.Dto.keyword.KeywordUserDto;
import Project.OpenBook.Service.KeywordService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;


    @ApiOperation(value = "키워드 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "키워드 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 키워드 생성 실패"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 키워드 이름 입력")
    })
    @PostMapping("admin/keywords")
    public ResponseEntity createService(@Validated @RequestBody KeywordCreateDto keywordCreateDto) throws IOException {
        keywordService.createKeyword(keywordCreateDto);

        return new ResponseEntity(HttpStatus.CREATED);
    }


    @ApiOperation(value = "키워드 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인하여 키워드 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 키워드 수정 시도"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 키워드 이름 입력")
    })
    @PatchMapping("admin/keywords/{keywordId}")
    public ResponseEntity updateKeyword(@PathVariable("keywordId") Long keywordId,
                                        @Validated @RequestBody KeywordUserDto keywordUserDto) throws IOException {
        keywordService.updateKeyword(keywordId, keywordUserDto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "키워드 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "키워드 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재히지 않는 키워드 삭제 시도")
    })
    @DeleteMapping("admin/keywords/{keywordId}")
    public ResponseEntity deleteKeyword(@PathVariable("keywordId") Long keywordId) {
        keywordService.deleteKeyword(keywordId);

        return new ResponseEntity(HttpStatus.OK);
    }
}

