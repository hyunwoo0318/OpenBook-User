package Project.OpenBook.Constants;

import java.util.HashMap;
import java.util.Map;

public enum ChoiceType {
    String, Image;

    public static Map<String, ChoiceType> getChoiceTypeNameMap() {
        Map<String, ChoiceType> map = new HashMap<>();
        ChoiceType[] values = values();
        for (ChoiceType value : values) {
            map.put(value.name(), value);
        }
        return map;
    }
}
