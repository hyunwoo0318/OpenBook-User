package Project.OpenBook.Domain.JJH.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JJHListAdminQueryDto {

    private List<ChapterJJHAdminQueryDto> chapterList = new ArrayList<>();

    private List<TimelineJJHAdminQueryDto> timelineList = new ArrayList<>();
}
