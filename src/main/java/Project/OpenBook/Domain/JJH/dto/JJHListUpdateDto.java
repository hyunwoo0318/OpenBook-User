package Project.OpenBook.Domain.JJH.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JJHListUpdateDto {
    private List<JJHUpdateDto> chapterList = new ArrayList<>();
    private List<JJHUpdateDto> timelineList = new ArrayList<>();
}
