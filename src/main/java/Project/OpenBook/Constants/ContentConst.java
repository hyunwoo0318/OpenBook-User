package Project.OpenBook.Constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ContentConst {
    CHAPTER_INFO("단원 학습"),
    TIMELINE_STUDY("연표 학습"),
    TOPIC_STUDY("주제 학습"),
    CHAPTER_COMPLETE_QUESTION("단원 마무리 문제"),

    ;
    private final String name;

}
