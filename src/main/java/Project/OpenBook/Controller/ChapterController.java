package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Domain.Topic;
import Project.OpenBook.Dto.chapter.ChapterDto;
import Project.OpenBook.Dto.chapter.ChapterListDto;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.topic.TopicTitleListDto;
import Project.OpenBook.Service.ChapterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/chapters")
@CrossOrigin(allowedHeaders = "*", origins = "*")
@Slf4j
public class ChapterController {

    private final ChapterService chapterService;


    @ApiOperation(value= "모든 단원 정보 가져오기", notes = "단원 제목과 단원 번호를 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 전체 조회 성공")
    })
    @GetMapping
    public ResponseEntity getChapter(){
        List<String> titleList = new ArrayList<>();
        List<Integer> numList = new ArrayList<>();

        List<Chapter> chapterList = chapterService.getAllChapter();
        for (Chapter chapter : chapterList) {
            titleList.add(chapter.getTitle());
            numList.add(chapter.getNumber());
        }

        ChapterListDto chapterListDto = new ChapterListDto(titleList, numList);
        return new ResponseEntity(chapterListDto, HttpStatus.OK);
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

    @ApiOperation(value = "단원 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "단원 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 변경 시도"),
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
