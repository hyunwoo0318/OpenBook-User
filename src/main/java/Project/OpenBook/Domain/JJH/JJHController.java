package Project.OpenBook.Domain.JJH;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JJHController {

    private final JJHService jjhService;
    @GetMapping("/jjh")
    public ResponseEntity queryJJH() {
        JJHQueryListDto listDto = jjhService.queryJJH();
        return new ResponseEntity(listDto, HttpStatus.OK);
    }

    @PatchMapping("/jjh")
    public ResponseEntity updateJJHList(@Validated @RequestBody JJHListUpdateDto dto) {
        jjhService.updateJJHList(dto);
        return new ResponseEntity( HttpStatus.OK);
    }
}
