package Project.OpenBook.Domain.JJH;

import Project.OpenBook.Domain.TimeLine.TimelineQueryDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JJHQueryListDto {

    private List<ChapterJJHQueryDto> chapterList = new ArrayList<>();

    private List<TimelineQueryDto> timelineList = new ArrayList<>();
}
