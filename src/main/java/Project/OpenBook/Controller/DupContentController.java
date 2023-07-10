package Project.OpenBook.Controller;

import Project.OpenBook.Domain.DupContent;
import Project.OpenBook.Dto.choice.ChoiceDto;
import Project.OpenBook.Dto.choice.ChoiceIdDto;
import Project.OpenBook.Dto.choice.ChoiceIdListDto;
import Project.OpenBook.Service.ChoiceService;
import Project.OpenBook.Service.DupContentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DupContentController {

    private final DupContentService dupContentService;

    @ApiOperation("해당 보기와 내용이 겹치는 선지 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적인 조회"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 보기 ID입력")
    })
    @GetMapping("/admin/dup-contents/{descriptionId}")
    public ResponseEntity queryDupContentChoices(@PathVariable Long descriptionId) {
        List<ChoiceDto> choiceDtoList = dupContentService.queryDupContentChoices(descriptionId);

        return new ResponseEntity(choiceDtoList, HttpStatus.OK);
    }



    @ApiOperation("해당 보기와 내용이 겹치는 선지들 선정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "선지들 선정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 보기나 선지 입력")
    })
    @PostMapping("/admin/dup-contents/{descriptionId}")
    public ResponseEntity addDupContentChoices(@PathVariable Long descriptionId,@Validated @RequestBody ChoiceIdListDto choiceIdListDto) {
        List<DupContent> dupContentList = dupContentService.addDupContentChoices(descriptionId, choiceIdListDto);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation("해당 보기와 내용이 겹치는 선지 목록중 특정 선지 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내용이 겹치는 선지 목록중 특정 선지 제거 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 보기 id 입력")
    })
    @DeleteMapping("/admin/dup-contents/{descriptionId}")
    public ResponseEntity deleteDupContentChoices(@PathVariable Long descriptionId,@Validated @RequestBody ChoiceIdDto choiceIdDto){
        dupContentService.deleteDupContentChoices(descriptionId, choiceIdDto.getChoiceId());

        return new ResponseEntity(HttpStatus.OK);
    }

}
