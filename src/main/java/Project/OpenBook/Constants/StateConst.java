package Project.OpenBook.Constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StateConst {

    OPEN("Open"),
    LOCKED("Locked"),
    UPDATED("Updated")

    ;
    private final String name;

}
