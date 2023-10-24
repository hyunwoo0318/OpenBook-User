package Project.OpenBook.Domain.JJH.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JJHListCustomerQueryDto {
    private List<ChapterJJHCustomerQueryDto> chapterList = new ArrayList<>();
    private List<TimelineJJHCustomerQueryDto> timelineList = new ArrayList<>();
}
