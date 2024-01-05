package Project.OpenBook.Domain.JJH;

import static Project.OpenBook.Constants.JJHForFreeConst.JJH_NUMBER_FREE_LIMIT;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.JJH.dto.ContentProgressUpdateDto;
import Project.OpenBook.Domain.JJH.dto.JJHContentsTableQueryDto;
import Project.OpenBook.Domain.JJH.dto.JJHListCustomerQueryDto;
import Project.OpenBook.Domain.JJH.dto.TotalProgressDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JJHController {

    private final JJHService jjhService;


    @Operation(summary = "사용자 페이지에서 정주행 리스트 조회")
    @GetMapping("/jjh")
    public ResponseEntity<JJHListCustomerQueryDto> queryJJHCustomer(
        @Parameter(hidden = true) @AuthenticationPrincipal Customer customer) {
        JJHListCustomerQueryDto dto = null;
        if (customer == null) {
            dto = jjhService.queryJJHCustomerForFree();
        } else {
            dto = jjhService.queryJJHCustomer(customer);
        }
        return new ResponseEntity<JJHListCustomerQueryDto>(dto, HttpStatus.OK);
    }


    @Operation(summary = "해당 jjhNumber를 가진 contents들 조회")
    @GetMapping("/jjh/{jjhNumber}/contents-table")
    public ResponseEntity<List<JJHContentsTableQueryDto>> queryJJHContentsTable(
        @Parameter(hidden = true) @AuthenticationPrincipal Customer customer,
        @PathVariable Integer jjhNumber) {
        List<JJHContentsTableQueryDto> dtoList = null;
        if (customer == null && jjhNumber <= JJH_NUMBER_FREE_LIMIT) {
            dtoList = jjhService.queryJJHContentsTableForFree(jjhNumber);
        } else {
            dtoList = jjhService.queryJJHContentsTable(customer, jjhNumber);
        }
        return new ResponseEntity<List<JJHContentsTableQueryDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "progress 갱신")
    @PatchMapping("/jjh/progress")
    public ResponseEntity updateProgress(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer,
        @Validated @RequestBody ContentProgressUpdateDto dto) {
        jjhService.updateProgress(customer, dto);
        return new ResponseEntity(HttpStatus.OK);
    }

    @Operation(summary = "전체 진도율 조회")
    @GetMapping("/total-progress")
    public ResponseEntity<TotalProgressDto> queryTotalProgress(
        @Parameter(hidden = true) @AuthenticationPrincipal(errorOnInvalidType = true) Customer customer) {
        TotalProgressDto dto = jjhService.queryTotalProgress(customer);
        return new ResponseEntity<TotalProgressDto>(dto, HttpStatus.OK);
    }


}
