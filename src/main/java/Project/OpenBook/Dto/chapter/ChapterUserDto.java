package Project.OpenBook.Dto.chapter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterUserDto {

    private String title;
    private Integer number;
    private String state;
    private String progress;
    private List<String> topicList = new ArrayList<>();

}
