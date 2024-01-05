package Project.OpenBook.Domain.Chapter.Controller;


import Project.OpenBook.Domain.Chapter.Service.ChapterService;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterDetailDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterInfoDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterTitleDto;
import Project.OpenBook.Domain.Chapter.Service.dto.ChapterUserDto;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@Slf4j
public class ChapterController {

    private final ChapterService chapterService;

    @Operation(summary = "모든 단원 정보 가져오기 - 정주행")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "단원 전체 조회 성공")
    })
    @GetMapping("/jjh/chapters")
    public ResponseEntity<List<ChapterUserDto>> queryChapterUserJJH(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
        HttpServletRequest request) {
        //List<ChapterUserDto> chapterUserDtoList = chapterWithProgressService.queryChapterUserDtos(customer);
        return new ResponseEntity<List<ChapterUserDto>>(new ArrayList<>(), HttpStatus.OK);
    }

    @Operation(summary = "모든 단원 정보 가져오기 - 학습자료 모음")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "단원 전체 조회 성공")
    })
    @GetMapping("/chapters")
    public ResponseEntity<List<ChapterDetailDto>> queryChapters() {
        List<ChapterDetailDto> dtoList = chapterService.queryChaptersTotalInfo();
        return new ResponseEntity<List<ChapterDetailDto>>(dtoList, HttpStatus.OK);
    }


    @Operation(summary = "단원 이름 조회", description = "단원 번호를 넘기면 단원 이름을 알려주는 endPoint")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "단원 이름 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/chapters/chapter-title")
    public ResponseEntity<ChapterTitleDto> queryChapterTitle(@RequestParam("num") Integer num) {
        ChapterTitleDto dto = chapterService.queryChapterTitle(num);

        return new ResponseEntity<ChapterTitleDto>(dto, HttpStatus.OK);
    }

//    @Operation(summary = "단원 dateComment 조회")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "조회 성공"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
//    })
//    @GetMapping("/chapters/{num}/date")
//    public ResponseEntity<ChapterDateDto> queryChapterDate(@PathVariable("num") Integer num) {
//        ChapterDateDto dto = chapterSimpleQueryService.queryChapterDate(num);
//        return new ResponseEntity<ChapterDateDto>(dto, HttpStatus.OK);
//    }


    @Operation(summary = "단원 학습 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "단원 학습 조회 성공"),
        @ApiResponse(responseCode = "404", description = "존재하지 않는 단원 번호 입력")
    })
    @GetMapping("/chapters/{num}/info")
    public ResponseEntity<ChapterInfoDto> queryChapterInfoAdmin(@PathVariable("num") Integer num) {
        ChapterInfoDto dto = chapterService.queryChapterInfo(num);

        return new ResponseEntity<ChapterInfoDto>(dto, HttpStatus.OK);
    }


}
