package Project.OpenBook.Utils;

import Project.OpenBook.Constants.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CustomException extends RuntimeException{
    private ErrorCode errorCode;
}
