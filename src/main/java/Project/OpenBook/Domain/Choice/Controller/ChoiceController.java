//package Project.OpenBook.Domain.Choice.Controller;
//
//import Project.OpenBook.Domain.Choice.Service.ChoiceService;
//import Project.OpenBook.Domain.ExamQuestion.Service.dto.ChoiceAddUpdateDto;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.responses.ApiResponse;
//import io.swagger.v3.oas.annotations.responses.ApiResponses;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//
//@RestController
//@RequiredArgsConstructor
//public class ChoiceController {
//
//    private final ChoiceService choiceService;
//
//    @Operation(summary = "선지 수정")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "선지 수정 성공"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 선지id 입력")
//    })
//    @PatchMapping("/admin/choices/{choiceId}")
//    public ResponseEntity<Void> updateChoice(@PathVariable("choiceId") Long choiceId,
//                                             @Validated @RequestBody ChoiceAddUpdateDto choiceAddUpdateDto) throws IOException {
//        choiceService.updateChoice(choiceId, choiceAddUpdateDto);
//        return new ResponseEntity<Void>(HttpStatus.OK);
//    }
//
//    @Operation(summary = "선지 삭제")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "선지 삭제 성공"),
//            @ApiResponse(responseCode = "404", description = "존재하지 않는 선지id 입력")
//    })
//    @DeleteMapping("/admin/choices/{choiceId}")
//    public ResponseEntity<Void> deleteChoice(@PathVariable("choiceId") Long choiceId)  {
//        choiceService.deleteChoice(choiceId);
//        return new ResponseEntity<Void>(HttpStatus.OK);
//    }
//
//}
//
