package Project.OpenBook.Handler.Exception;

import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Handler.Exception.error.ErrorMsgDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ CustomException.class })
    protected ResponseEntity handleCustomException(CustomException ex) {
        ErrorMsgDto errorMsgDto = new ErrorMsgDto(ex.getErrorCode().getErrorMessage());
        List<ErrorMsgDto> errorMsgDtoList = Arrays.asList(errorMsgDto);
        return new ResponseEntity(errorMsgDtoList, ex.getErrorCode().getStatusCode());
    }

    @ExceptionHandler({BadCredentialsException.class})
    protected ResponseEntity handleBadCredentialException(BadCredentialsException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    protected ResponseEntity handleBindingException(MethodArgumentNotValidException e) {
        List<ErrorMsgDto> errorMsgDtoList = e.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorMsgDto(err.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity(errorMsgDtoList, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity handleServerException(Exception ex) {
        System.out.println(ex.toString());
        System.out.println("tracking : "  + ex.getStackTrace());
        return new ResponseEntity(ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
    }
}