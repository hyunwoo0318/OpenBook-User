package Project.OpenBook.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StateConst {

    IN_PROGRESS("InProgress"),
    LOCKED("Locked"),
    COMPLETE("Complete"),
    UPDATED("Updated");
    private final String name;

}
