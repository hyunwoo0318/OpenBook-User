package Project.OpenBook.Controller;

import Project.OpenBook.Domain.DupContent;
import Project.OpenBook.Dto.choice.ChoiceIdListDto;
import Project.OpenBook.Service.DupContentService;
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
public class DupContentController {

    private final DupContentService dupContentService;

    @ApiOperation("해당 보기와 내용이 겹치는 선지들 선정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "선지들 선정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 보기나 선지 입력")
    })
    @PostMapping("/admin/dup-contents/{descriptionId}")
    public ResponseEntity addDupContentChoices(@PathVariable Long descriptionId,@RequestBody ChoiceIdListDto choiceIdListDto){
        List<DupContent> dupContentList = dupContentService.addDupContentChoices(descriptionId, choiceIdListDto);
        if (dupContentList == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @ApiOperation("해당 보기와 내용이 겹치는 선지 목록중 특정 선지 제거")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "내용이 겹치는 선지 목록중 특정 선지 제거 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 선지나 보기 입력")
    })
    @DeleteMapping("/admin/dup-contents/{descriptionId}")
    public ResponseEntity deleteDupContentChoices(@PathVariable Long descriptionId, @RequestBody Long choiceId){
        boolean res = dupContentService.deleteDupContentChoices(descriptionId, choiceId);
        if (!res) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

}
