package Project.OpenBook.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ContentConst {
    NOT_STARTED("시작 전",1),
    CHAPTER_INFO("단원 학습",2),
    TIME_FLOW_STUDY("연표 학습",3),
    TIME_FLOW_QUESTION("연표 문제",4),
    TOPIC_STUDY("주제 학습",5),
    GET_KEYWORD_BY_TOPIC("주제 보고 키워드 맞추기",6),
    GET_SENTENCE_BY_TOPIC("주제 보고 문장 맞추기",7),
    GET_TOPIC_BY_KEYWORD("키워드 보고 주제 맞추기",8),
    GET_TOPIC_BY_SENTENCE("문장 보고 주제 맞추기",9),
    COMPLETE("완료",10)

    ;
    private final String name;
    private final Integer order;
}
