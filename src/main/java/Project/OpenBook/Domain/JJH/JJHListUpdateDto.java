package Project.OpenBook.Domain.JJH;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JJHListUpdateDto {
    private List<jjhUpdateDto> chapterList = new ArrayList<>();
    private List<jjhUpdateDto> timelineList = new ArrayList<>();
}
