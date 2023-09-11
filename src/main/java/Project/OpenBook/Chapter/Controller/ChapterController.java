package Project.OpenBook.Chapter.Controller;


import Project.OpenBook.Chapter.Controller.dto.*;
import Project.OpenBook.Chapter.Service.ChapterService;
import Project.OpenBook.Chapter.Service.ChapterWithProgressService;
import Project.OpenBook.Chapter.Domain.Chapter;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Dto.studyProgress.ProgressDto;

import Project.OpenBook.Topic.Domain.Topic;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ChapterController {

    private final ChapterService chapterService;
    private final ChapterWithProgressService chapterWithProgressService;


    @ApiOperation(value= "모든 단원 정보 가져오기 - 관리자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 전체 조회 성공")
    })
    @GetMapping("/admin/chapters")
    public ResponseEntity<List<ChapterTitleNumDto>> queryChaptersAdmin(){
        List<ChapterTitleNumDto> dtoList = chapterService.queryChaptersAdmin();

        return new ResponseEntity<List<ChapterTitleNumDto>>(dtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "모든 단원 정보 가져오기 - 정주행")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 전체 조회 성공")
    })
    @GetMapping("/jjh/chapters")
    public ResponseEntity<List<ChapterUserDto>> queryChapterUserJJH(@AuthenticationPrincipal Customer customer, HttpServletRequest request){
        List<ChapterUserDto> chapterUserDtoList = chapterWithProgressService.queryChapterUserDtos(customer);
        return new ResponseEntity<List<ChapterUserDto>>(chapterUserDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "모든 단원 정보 가져오기 - 학습자료 모음")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 전체 조회 성공")
    })
    @GetMapping("/chapters")
    public ResponseEntity<List<ChapterDetailDto>> queryChapters() {
        List<ChapterDetailDto> dtoList = chapterService.queryChaptersTotalInfo();
        return new ResponseEntity<List<ChapterDetailDto>>(dtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "목차 제공 - 사용자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "목차 제공 성공")
    })
    @GetMapping("/contents-table")
    public ResponseEntity<List<ProgressDto>> queryContentTable(@AuthenticationPrincipal Customer customer, @RequestParam("num") Integer number) {
        List<ProgressDto> contentTable = chapterWithProgressService.queryContentTable(customer, number);
        return new ResponseEntity<List<ProgressDto>>(contentTable, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 이름 조회", notes = "단원 번호를 넘기면 단원 이름을 알려주는 endPoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 이름 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/chapters/chapter-title")
    public ResponseEntity<ChapterTitleDto> queryChapterTitle(@RequestParam("num") Integer num){
        ChapterTitleDto dto = chapterService.queryChapterTitle(num);

        return new ResponseEntity<ChapterTitleDto>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 시작년도/종료년도 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/chapters/{num}/date")
    public ResponseEntity<ChapterDateDto> queryChapterDate(@PathVariable("num") Integer num) {
        ChapterDateDto dto = chapterService.queryChapterDate(num);
        return new ResponseEntity<ChapterDateDto>(dto, HttpStatus.OK);
    }


    @ApiOperation(value = "단원 학습 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 학습 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/chapters/{num}/info")
    public ResponseEntity<ChapterInfoDto> queryChapterInfoAdmin(@PathVariable("num") Integer num){
        ChapterInfoDto dto = chapterService.queryChapterInfo(num);

        return new ResponseEntity<ChapterInfoDto>(dto, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 학습 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 설명 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @PatchMapping("/admin/chapters/{num}/info")
    public ResponseEntity<ChapterInfoDto> updateChapterInfo(@PathVariable("num") Integer num, @Validated @RequestBody ChapterInfoDto inputChapterInfoDto){
        ChapterInfoDto chapterInfoDto = chapterService.updateChapterInfo(num, inputChapterInfoDto.getContent());

        return new ResponseEntity<ChapterInfoDto>(chapterInfoDto, HttpStatus.OK);
    }

    @ApiOperation("해당 단원의 모든 topic 조회 - 관리자")
    @GetMapping("/admin/chapters/{num}/topics")
    public ResponseEntity<List<ChapterTopicWithCountDto>> queryChapterTopicsAdmin(@PathVariable("num") int num) {
        List<ChapterTopicWithCountDto> dtoList = chapterService.queryChapterTopicsAdmin(num);

        return new ResponseEntity<List<ChapterTopicWithCountDto>>(dtoList, HttpStatus.OK);
    }

    @ApiOperation("해당 단원의 모든 topic 조회 - 사용자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 topic 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력"),
    })
    @GetMapping("/chapters/{num}/topics")
    public ResponseEntity<List<ChapterTopicUserDto>> queryChapterTopicsCustomer(@PathVariable("num") int num){
        List<ChapterTopicUserDto> dtoList = chapterService.queryChapterTopicsCustomer(num);

        return new ResponseEntity<List<ChapterTopicUserDto>>(dtoList, HttpStatus.OK);
    }


    @ApiOperation(value = "단원 추가", notes = "단원제목과 단원번호를 입력해서 새로운 단원 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "단원 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 단원 추가 실패"),
            @ApiResponse(responseCode = "409",  description = "중복된 단원 번호 입력")
    })
    @PostMapping("/admin/chapters")
    public ResponseEntity<Void> addChapter(@Validated @RequestBody ChapterAddUpdateDto chapterAddUpdateDto) {
        chapterService.createChapter(chapterAddUpdateDto);

        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @ApiOperation(value = "단원 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 수정 시도"),
            @ApiResponse(responseCode = "409",  description = "중복된 단원 번호 입력")
    })
    @PatchMapping("/admin/chapters/{num}")
    public ResponseEntity<Void> updateChapter(@PathVariable("num") int num, @Validated @RequestBody ChapterAddUpdateDto chapterAddUpdateDto) {
        chapterService.updateChapter(num,chapterAddUpdateDto);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation(value = "단원 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "토픽이 존재하는 단원을 삭제 시도하는 경우"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 삭제 시도")
    })
    @DeleteMapping("/admin/chapters/{num}")
    public ResponseEntity<Void> deleteChapter(@PathVariable("num") int num) {
        Boolean ret = chapterService.deleteChapter(num);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
