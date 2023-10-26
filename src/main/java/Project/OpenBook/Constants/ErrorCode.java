package Project.OpenBook.Constants;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@JsonFormat(shape= JsonFormat.Shape.OBJECT)
@AllArgsConstructor
@Getter
public enum ErrorCode {

    /**
     * 400 - BAD_REQUEST
     */
    INVALID_PARAMETER (HttpStatus.BAD_REQUEST, "파라미터 값을 확인해주세요."),
    CHAPTER_HAS_TOPIC(HttpStatus.BAD_REQUEST, "해당 단원에 토픽이 존재합니다."),
    CATEGORY_HAS_TOPIC(HttpStatus.BAD_REQUEST, "해당 카테고리에 토픽이 존재합니다"),
    ERA_HAS_TOPIC(HttpStatus.BAD_REQUEST, "해당 시대에 토픽이 존재합니다."),
    TOPIC_HAS_CHOICE(HttpStatus.BAD_REQUEST, "해당 토픽에 선지가 존재합니다."),
    TOPIC_HAS_DESCRIPTION(HttpStatus.BAD_REQUEST, "해당 토픽에 보기가 존재합니다."),
    TOPIC_HAS_KEYWORD(HttpStatus.BAD_REQUEST, "해당 토픽에 키워드가 존재합니다."),
    ROUND_HAS_QUESTION(HttpStatus.BAD_REQUEST, "해당 회차에 문제가 존재합니다."),
    NOT_ENOUGH_CHOICE(HttpStatus.BAD_REQUEST, "문제를 만들기 위한 선지의 수가 부족합니다."),
    QUESTION_ERROR(HttpStatus.BAD_REQUEST, "해당 유형의 문제를 생성할수 없습니다."),
    KEYWORD_HAS_TOPIC(HttpStatus.BAD_REQUEST, "해당 키워드를 가지는 토픽이 존재합니다."),
    NOT_SAVED_CHOICE(HttpStatus.BAD_REQUEST, "해당 보기와 내용이 겹친 선지가 아닙니다."),
    WRONG_PROVIDER_NAME(HttpStatus.BAD_REQUEST, "잘못된 소셜 로그인 provider name입니다."),
    NOT_VALIDATE_IMAGE(HttpStatus.BAD_REQUEST, "잘못된 이미지 파일입니다."),
    NOT_VALIDATE_CHOICE_TYPE(HttpStatus.BAD_REQUEST, "잘못된 Type입니다."),
    NOT_VALIDATE_CONTENT_NUMBER(HttpStatus.BAD_REQUEST, "잘못된 번호입니다."),

    /**
     * 401 - UNAUTHORIZED
     */
    LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "로그인에 실패하였습니다."),
    NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "허용되지 않은 사용자입니다"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다"),

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
    KEYWORD_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 키워드 이름입니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 이미지 ID입니다."),
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 Content 이름입니다."),
    STATE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 State 이름입니다."),
    ROUND_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 round번호 입니다."),
    ERA_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 시대입니다."),
    TIMELINE_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 timeline입니다."),
    QUESTION_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 questionCategory입니다."),


    /**
     * 409 - CONFLICT
     */
    DUP_NICKNAME(HttpStatus.CONFLICT, "중복된 닉네임입니다."),
    DUP_CHAPTER_NUM(HttpStatus.CONFLICT, "중복된 단원 번호입니다."),
    DUP_CATEGORY_NAME(HttpStatus.CONFLICT, "중복된 카테고리 이름입니다."),
    DUP_TOPIC_TITLE(HttpStatus.CONFLICT, "중복된 토픽 제목입니다."),
    DUP_CHOICE_CONTENT(HttpStatus.CONFLICT, "중복된 선지내용입니다."),
    DUP_DESCRIPTION_CONTENT(HttpStatus.CONFLICT, "중복된 보기내용입니다."),
    DUP_KEYWORD_NAME(HttpStatus.CONFLICT, "중복된 키워드 이름이 해당 토픽 내에 존재합니다"),
    DUP_TOPIC_NUMBER(HttpStatus.CONFLICT, "중복된 단원 내 주제 번호입니다."),
    DUP_ROUND_NUMBER(HttpStatus.CONFLICT, "중복된 회차 번호입니다."),
    DUP_QUESTION_NUMBER(HttpStatus.CONFLICT, "중복된 문제 번호입니다."),
    DUP_ERA_NAME(HttpStatus.CONFLICT, "중복된 시대입니다."),


    /**
     * 503 -  Service Unavailable
     */
    IMAGE_SAVE_FAIL(HttpStatus.SERVICE_UNAVAILABLE, "이미지 저장에 실패했습니다."),
    CONNECTION_FAIL(HttpStatus.SERVICE_UNAVAILABLE, "연결에 실패했습니다."),
    ;

    private final HttpStatus statusCode;
    private final String errorMessage;
}
