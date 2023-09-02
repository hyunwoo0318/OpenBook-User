package Project.OpenBook.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum ContentConst {
    NOT_STARTED("시작 전",1),
    CHAPTER_INFO("단원 학습",2),
    TIME_FLOW_STUDY("연표 학습",3),
    TOPIC_STUDY("주제 학습",4),
    CHAPTER_COMPLETE_QUESTION("단원 마무리 문제",5),
    COMPLETE("완료",6)

    ;
    private final String name;
    private final Integer order;

    public static List<String> getChapterContent() {
        return Arrays.asList(CHAPTER_INFO.getName(),
                TIME_FLOW_STUDY.getName(),CHAPTER_COMPLETE_QUESTION.getName());
    }

    public static List<String> getTopicContent() {
        return Arrays.asList(TOPIC_STUDY.getName());
    }
}
