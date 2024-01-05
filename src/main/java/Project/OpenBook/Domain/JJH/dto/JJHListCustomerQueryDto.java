package Project.OpenBook.Domain.JJH.dto;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JJHListCustomerQueryDto {

    private List<ChapterJJHCustomerQueryDto> chapterList = new ArrayList<>();
    private List<TimelineJJHCustomerQueryDto> timelineList = new ArrayList<>();
}
