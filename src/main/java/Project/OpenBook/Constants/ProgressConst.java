package Project.OpenBook.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ProgressConst {
    
    NOT_STARTED("시작 전"),
    IN_PROGRESS("진행 중"),
    COMPLETED("완료")

    ;
    
    private final String name;
}
