package Project.OpenBook.Domain.Round.Controller;

import Project.OpenBook.Domain.Round.Service.RoundService;
import Project.OpenBook.Domain.Round.dto.RoundDto;
import Project.OpenBook.Domain.Round.dto.RoundInfoDto;
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
public class RoundController {

    private final RoundService roundService;

    @ApiOperation("전체 회차 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 회차 조회 성공")
    })
    @GetMapping("/rounds")
    public ResponseEntity<List<RoundDto>> getRounds() {
        List<RoundDto> dtoList = roundService.queryRounds();
        return new ResponseEntity<List<RoundDto>>(dtoList, HttpStatus.OK);
    }

    @ApiOperation("특정 회차 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 회차 조회 성공")
    })
    @GetMapping("/rounds/{number}")
    public ResponseEntity<RoundInfoDto> getRoundQuestion(@PathVariable("number") Integer number) {
        RoundInfoDto dto = roundService.queryRound(number);
        return new ResponseEntity<RoundInfoDto>(dto, HttpStatus.OK);
    }

    @ApiOperation("회차 내 전체 문제 번호 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 회차 조회 성공")
    })
    @GetMapping("/rounds/{number}/questions")
    public ResponseEntity<List<Integer>> getRounds(@PathVariable("number") Integer number) {
        List<Integer> dtoList = roundService.queryRoundQuestions(number);
        return new ResponseEntity<List<Integer>>(dtoList, HttpStatus.OK);
    }


    @ApiOperation("회차 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회차 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력")
    })
    @PostMapping("/admin/rounds")
    public ResponseEntity<Void> createRound(@Validated @RequestBody RoundDto roundDto) {
        roundService.createRound(roundDto);
        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }

    @ApiOperation("회차 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회차 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호 입력")
    })
    @PatchMapping("/admin/rounds/{number}")
    public ResponseEntity<Void> updateRound(@PathVariable("number") Integer prevNumber, @Validated @RequestBody RoundDto roundDto) {
        roundService.updateRound(prevNumber, roundDto);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @ApiOperation("회차 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회차 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 회차 번호 입력")
    })
    @DeleteMapping("/admin/rounds/{number}")
    public ResponseEntity<Void> deleteRound(@PathVariable("number") Integer number) {
        roundService.deleteRound(number);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }


}
