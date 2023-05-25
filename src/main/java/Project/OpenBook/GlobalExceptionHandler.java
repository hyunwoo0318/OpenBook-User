package Project.OpenBook;

import Project.OpenBook.Dto.error.ErrorDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ CustomException.class })
    protected ResponseEntity handleCustomException(CustomException ex) {
        return new ResponseEntity(ex.getErrorCode().getErrorMessage(), ex.getErrorCode().getStatusCode());
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
        List<ErrorDto> errorDtoList = e.getBindingResult().getFieldErrors().stream()
                .map(err -> new ErrorDto(err.getField(), err.getDefaultMessage()))
                .collect(Collectors.toList());
        return new ResponseEntity(errorDtoList, HttpStatus.BAD_REQUEST);
    }
}