package Project.OpenBook.Controller;


import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Dto.chapter.*;
import Project.OpenBook.Dto.topic.AdminChapterDto;
import Project.OpenBook.Service.ChapterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ChapterController {

    private final ChapterService chapterService;


    @ApiOperation(value= "모든 단원 정보 가져오기 - 관리자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 전체 조회 성공")
    })
    @GetMapping("/admin/chapters")
    public ResponseEntity queryChapterAdmin(){
        List<ChapterDto> chapterDtoList = chapterService.queryAllChapters();

        return new ResponseEntity(chapterDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "모든 단원 정보 가져오기 - 사용자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 전체 조회 성공")
    })
    @GetMapping("/chapters")
    public ResponseEntity queryChapterUser(@AuthenticationPrincipal Customer customer){
        List<ChapterUserDto> chapterUserDtoList = chapterService.queryChapterUserDtos(customer);
        return new ResponseEntity(chapterUserDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 이름 조회", notes = "단원 번호를 넘기면 단원 이름을 알려주는 endPoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 이름 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/admin/chapters/chapter-title")
    public ResponseEntity queryChapterTitle(@RequestParam("num") Integer num){
        ChapterTitleDto chapterTitleDto = chapterService.queryChapterTitle(num);

        return new ResponseEntity(chapterTitleDto, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 학습 조회 - 관리자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 학습 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/admin/chapters/{num}/info")
    public ResponseEntity queryChapterInfoAdmin(@PathVariable("num") Integer num){
        ChapterInfoDto chapterInfoDto = chapterService.queryChapterInfoAdmin(num);

        return new ResponseEntity(chapterInfoDto, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 제목/학습 조회 - 사용자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 학습 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/chapters/{num}/info")
    public ResponseEntity queryChapterInfoCustomer(@PathVariable("num") Integer num){
        ChapterTitleInfoDto chapterTitleInfoDto = chapterService.queryChapterInfoCustomer(num);

        return new ResponseEntity(chapterTitleInfoDto, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 학습과 단원 이름 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원학습 단원이름 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/admin/chapters/title-info")
    public ResponseEntity queryChapterTitleInfo(@RequestParam("num") Integer num) {
        ChapterTitleInfoDto chapterTitleInfoDto = chapterService.queryChapterTitleInfo(num);

        return new ResponseEntity(chapterTitleInfoDto, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 학습 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 설명 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @PatchMapping("/admin/chapters/{num}/info")
    public ResponseEntity updateChapterInfo(@PathVariable("num") Integer num, @Validated @RequestBody ChapterInfoDto chapterInfoDto){
        chapterService.updateChapterInfo(num,chapterInfoDto.getContent());

        return new ResponseEntity(chapterInfoDto, HttpStatus.OK);
    }

    @ApiOperation("해당 단원의 모든 topic 조회 - 관리자")
    @GetMapping("/admin/chapters/{num}/topics")
    public ResponseEntity queryChapterTopicsAdmin(@PathVariable("num") int num) {
        List<AdminChapterDto> adminChapterDtoList = chapterService.queryTopicsInChapterAdmin(num);
        return new ResponseEntity(adminChapterDtoList, HttpStatus.OK);
    }

    @ApiOperation("해당 단원의 모든 topic 조회 - 사용자")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 topic 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력"),
    })
    @GetMapping("/chapters/{num}/topics")
    public ResponseEntity queryChapterTopicsCustomer(@PathVariable("num") int num){
        List<String> topicTitleList = chapterService.queryTopicsInChapterCustomer(num);
        return new ResponseEntity(topicTitleList, HttpStatus.OK);
    }


    @ApiOperation(value = "단원 추가", notes = "단원제목과 단원번호를 입력해서 새로운 단원 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "단원 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 단원 추가 실패"),
            @ApiResponse(responseCode = "409",  description = "중복된 단원 번호 입력")
    })
    @PostMapping("/admin/chapters")
    public ResponseEntity addChapter(@Validated @RequestBody ChapterDto chapterDto) {
        Chapter chapter = chapterService.createChapter(chapterDto.getTitle(), chapterDto.getNumber());

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation(value = "단원 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 수정 시도"),
            @ApiResponse(responseCode = "409",  description = "중복된 단원 번호 입력")
    })
    @PatchMapping("/admin/chapters/{num}")
    public ResponseEntity updateChapter(@PathVariable("num") int num, @Validated @RequestBody ChapterDto chapterDto) {
        Chapter chapter = chapterService.updateChapter(num, chapterDto.getTitle(), chapterDto.getNumber());

        return new ResponseEntity(HttpStatus.OK);
    }

    @ApiOperation(value = "단원 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "토픽이 존재하는 단원을 삭제 시도하는 경우"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 삭제 시도")
    })
    @DeleteMapping("/admin/chapters/{num}")
    public ResponseEntity deleteChapter(@PathVariable("num") int num) {
        chapterService.deleteChapter(num);

        return new ResponseEntity(HttpStatus.OK);
    }
}
