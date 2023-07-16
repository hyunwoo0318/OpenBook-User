package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Dto.chapter.ChapterDto;
import Project.OpenBook.Dto.chapter.ChapterInfoDto;
import Project.OpenBook.Dto.chapter.ChapterNumDto;
import Project.OpenBook.Dto.chapter.ChapterTitleDto;
import Project.OpenBook.Dto.topic.AdminChapterDto;
import Project.OpenBook.Service.ChapterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/chapters")
@Slf4j
public class ChapterController {

    private final ChapterService chapterService;


    @ApiOperation(value= "모든 단원 정보 가져오기")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 전체 조회 성공")
    })
    @GetMapping
    public ResponseEntity queryChapter(){
        List<Chapter> chapterList = chapterService.queryAllChapters();
        List<ChapterDto> chapterDtoList = chapterList.stream().map(c -> new ChapterDto(c.getTitle(), c.getNumber())).collect(Collectors.toList());

        return new ResponseEntity(chapterDtoList, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 이름 조회", notes = "단원 번호를 넘기면 단원 이름을 알려주는 endPoint")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 이름 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/chapter-title")
    public ResponseEntity queryChapterTitle(@RequestParam("num") Integer num){
        String title = chapterService.queryChapterTitle(num);
        ChapterTitleDto chapterTitleDto = new ChapterTitleDto(title);

        return new ResponseEntity(chapterTitleDto, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 설명 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 설명 조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/{num}/info")
    public ResponseEntity queryChapterInfo(@PathVariable("num") Integer num){
        String info = chapterService.queryChapterInfo(num);
        ChapterInfoDto chapterInfoDto = new ChapterInfoDto(info);

        return new ResponseEntity(chapterInfoDto, HttpStatus.OK);
    }

    @ApiOperation(value = "단원 설명 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 설명 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @PatchMapping("/{num}/info")
    public ResponseEntity updateChapterInfo(@PathVariable("num") Integer num, @Validated @RequestBody ChapterInfoDto chapterInfoDto){
        chapterService.updateChapterInfo(num,chapterInfoDto.getContent());

        return new ResponseEntity(chapterInfoDto, HttpStatus.OK);
    }

    @ApiOperation("해당 단원의 모든 topic 조회")
    @GetMapping("/{num}/topics")
    public ResponseEntity queryChapterTopics(@PathVariable("num") int num) {
        List<AdminChapterDto> adminChapterDtoList = chapterService.queryTopicsInChapter(num);
        return new ResponseEntity(adminChapterDtoList, HttpStatus.OK);
    }


    @ApiOperation(value = "단원 추가", notes = "단원제목과 단원번호를 입력해서 새로운 단원 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "단원 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 단원 추가 실패"),
            @ApiResponse(responseCode = "409",  description = "중복된 단원 번호 입력")
    })
    @PostMapping
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
    @PatchMapping("/{num}")
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
    @DeleteMapping("/{num}")
    public ResponseEntity deleteChapter(@PathVariable("num") int num) {
        chapterService.deleteChapter(num);

        return new ResponseEntity(HttpStatus.OK);
    }
}
