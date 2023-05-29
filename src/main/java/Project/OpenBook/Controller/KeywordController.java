package Project.OpenBook.Controller;

import Project.OpenBook.Dto.keyword.KeywordDto;
import Project.OpenBook.Service.KeywordService;
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
public class KeywordController {

    private final KeywordService keywordService;

    @ApiOperation(value = "전체 키워드 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 카테고리 조회 성공")
    })
    @GetMapping("/keywords")
    public ResponseEntity queryServices() {
        List<String> keywordList = keywordService.queryKeywords();
        return new ResponseEntity(keywordList, HttpStatus.OK);
    }


    @ApiOperation(value = "특정 키워드를 가지는 모든 토픽 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 키워드를 가지는 모든 토픽 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 키워드 이름 입력")
    })
    @GetMapping("/keywords/{keywordName}/topics")
    public ResponseEntity queryKeywordTopic(@PathVariable("keywordName") String keywordName) {
        List<String> topicTitleList = keywordService.queryKeywordTopic(keywordName);
        return new ResponseEntity(topicTitleList, HttpStatus.OK);
    }


    @ApiOperation(value = "카테고리 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "카테고리 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 카테고리 생성 실패"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 카테고리 이름 입력")
    })
    @PostMapping("admin/keywords")
    public ResponseEntity createService(@Validated @RequestBody KeywordDto keywordDto) {
        keywordService.createKeyword(keywordDto.getName());

        return new ResponseEntity(HttpStatus.CREATED);
    }


    @ApiOperation(value = "카테고리 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인하여 카테고리 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리 수정 시도"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 카테고리 이름 입력")
    })
    @PatchMapping("admin/keywords/{keywordName}")
    public ResponseEntity updateKeyword(@PathVariable("keywordName") String keywordName, @Validated @RequestBody KeywordDto keywordDto) {
        if(!keywordName.equals(keywordDto.getName())){
            keywordService.updateKeyword(keywordName, keywordDto.getName());
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "카테고리 삭제", notes = "해당 카테고리에 속하던 상세정보의 카테고리 정보는 null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재히지 않는 카테고리 삭제 시도")
    })
    @DeleteMapping("admin/keywords/{keywordName}")
    public ResponseEntity deleteKeyword(@PathVariable("keywordName") String keywordName) {
        keywordService.deleteKeyword(keywordName);

        return new ResponseEntity(HttpStatus.OK);
    }
}

