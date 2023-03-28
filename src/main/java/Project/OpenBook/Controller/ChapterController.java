package Project.OpenBook.Controller;

import Project.OpenBook.Domain.Chapter;
import Project.OpenBook.Dto.ChapterDto;
import Project.OpenBook.Dto.ErrorDto;
import Project.OpenBook.Service.ChapterService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/chapters")
public class ChapterController {

    private final ChapterService chapterService;

    @ApiOperation(value = "단원 추가", notes = "단원제목과 단원번호를 입력해서 새로운 단원 추가")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "단원 추가 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인한 단원 추가 실패")
    })
    @PostMapping
    public ResponseEntity addChapter(@Validated @RequestBody ChapterDto chapterDto, BindingResult bindingResult) {
        List<ErrorDto> errorList = new ArrayList<>();
        //올바른 입력 확인
        if (bindingResult.hasErrors()) {
            errorList = bindingResult.getFieldErrors().stream().map(err -> new ErrorDto(err.getField(), err.getDefaultMessage())).collect(Collectors.toList());
        }
        //저장 시도
        Chapter chapter = chapterService.addChapter(chapterDto.getTitle(), chapterDto.getNum());
        //중복된 단원 번호 체크
        if (chapter == null) {
            errorList.add(new ErrorDto("chapter", "이미 존재하는 단원 번호입니다. 다른 단원 번호를 입력해 주세요."));
        }

        //오류가 있는 경우
        if(!errorList.isEmpty()){
            return new ResponseEntity(errorList, HttpStatus.BAD_REQUEST);
        }

        //단원 추가 성공
        return new ResponseEntity(HttpStatus.CREATED);
    }
}
