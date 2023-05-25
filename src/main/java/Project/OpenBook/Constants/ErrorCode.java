package Project.OpenBook.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorCode {

    /**
     * 400 - BAD_REQUEST
     */
    INVALID_PARAMETER (HttpStatus.BAD_REQUEST, "파라미터 값을 확인해주세요."),
    CHAPTER_HAS_TOPIC(HttpStatus.BAD_REQUEST, "해당 단원에 토픽이 존재합니다."),
    CATEGORY_HAS_TOPIC(HttpStatus.BAD_REQUEST, "해당 카테고리에 토픽이 존재합니다"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다"),
    NOT_ENOUGH_CHOICE(HttpStatus.BAD_REQUEST, "문제를 만들기 위한 선지의 수가 부족합니다."),


    /**
     * 401 - UNAUTHORIZED
     */
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "로그인에 실패하였습니다."),
    NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "허용되지 않은 사용자입니다"),

    /**
     * 404 - NOT_FOUND
     */
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 회원 ID입니다."),
    CHAPTER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 단원 번호입니다."),
    QUESTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 문제 ID입니다."),
    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 토픽 제목입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리 제목입니다."),
    CHOICE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 선지 ID입니다."),
    DESCRIPTION_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 보기 ID입니다."),
    /**
     * 409 - CONFLICT
     */
    DUP_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임입니다."),
    DUP_CHAPTER_NUM(HttpStatus.CONFLICT, "중복된 단원 번호입니다."),
    DUP_CATEGORY_NAME(HttpStatus.CONFLICT, "중복된 카테고리 이름입니다."),
    DUP_TOPIC_TITLE(HttpStatus.CONFLICT, "중복된 토픽 제목입니다."),

    ;


    private final HttpStatus statusCode;
    private final String errorMessage;
}
