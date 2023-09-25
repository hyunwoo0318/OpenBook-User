package Project.OpenBook.Domain.Era;

import Project.OpenBook.Domain.Category.Service.CategoryService;
import Project.OpenBook.Domain.Category.Service.Dto.CategoryDto;
import io.swagger.v3.oas.annotations.Operation;
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
@RequestMapping("/admin/eras")
public class EraController {

    private final EraService eraService;

    @Operation(summary = "전체 시대 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 시대 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<EraDto>> queryEras() {
        List<EraDto> dtoList = eraService.queryEras();
        return new ResponseEntity<List<EraDto>>(dtoList, HttpStatus.OK);
    }

    @Operation(summary = "시대 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "시대 생성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 시대 생성 실패"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 시대 이름 입력")
    })
    @PostMapping
    public ResponseEntity<Void> createEra(@Validated @RequestBody EraDto eraDto) {
        eraService.createEra(eraDto);

        return new ResponseEntity<Void>(HttpStatus.CREATED);
    }


    @Operation(summary = "시대 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "시대 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 입력으로 인하여 시대 수정 실패"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 시대 수정 시도"),
            @ApiResponse(responseCode = "409", description = "이미 존재하는 시대 이름 입력")
    })
    @PatchMapping("/{eraName}")
    public ResponseEntity<Void> updateCategory(@PathVariable("eraName") String prevEraName, @Validated @RequestBody EraDto eraDto) {
        eraService.updateEra(prevEraName, eraDto);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    @Operation(summary = "시대 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "시대 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "해당 시대에 토픽이 존재하는경우"),
            @ApiResponse(responseCode = "404", description = "존재히지 않는 시대 삭제 시도")
    })
    @DeleteMapping("/{eraName}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("eraName") String eraName) {
        eraService.deleteEra(eraName);

        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
