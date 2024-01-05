package Project.OpenBook.Domain.Round.Controller;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Round.Service.RoundService;
import Project.OpenBook.Domain.Round.Service.dto.RoundAnswerNotedCountDto;
import Project.OpenBook.Domain.Round.Service.dto.RoundInfoDto;
import Project.OpenBook.Domain.Round.Service.dto.RoundQueryCustomerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RoundController {

    private final RoundService roundService;


    @Operation(summary = "사용자 페이지에서 전체 회차 조회")
    @GetMapping("/rounds")
    public ResponseEntity getRoundsCustomer(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer) {
        List<RoundQueryCustomerDto> dtoList = roundService.queryRoundsCustomer(customer);
        return new ResponseEntity(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "사용자 페이지에서 회차별 오답노트 count 조회")
    @GetMapping("/rounds/answer-notes")
    public ResponseEntity<List<RoundAnswerNotedCountDto>> getRoundsAnswerNotedCount(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer
    ) {
        List<RoundAnswerNotedCountDto> dtoList = roundService.queryRoundsAnswerNotedCount(customer);
        return new ResponseEntity<List<RoundAnswerNotedCountDto>>(dtoList, HttpStatus.OK);
    }


    @Operation(summary = "특정 회차 조회")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "특정 회차 조회 성공")
    })
    @GetMapping("/rounds/{number}")
    public ResponseEntity<RoundInfoDto> getRoundQuestion(@PathVariable("number") Integer number) {
        RoundInfoDto dto = roundService.queryRound(number);
        return new ResponseEntity<RoundInfoDto>(dto, HttpStatus.OK);
    }


}
