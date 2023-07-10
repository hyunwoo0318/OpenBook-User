package Project.OpenBook.Handler;

import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Dto.error.ErrorMsgDto;
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

    @ExceptionHandler({ Exception.class })
    protected ResponseEntity handleServerException(Exception ex) {
        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
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
}