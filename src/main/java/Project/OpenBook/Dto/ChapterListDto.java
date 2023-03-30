package Project.OpenBook.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Schema(name = "ChapterListDto", description = "Chapter 조회에 사용되는 DTO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChapterListDto {
    private List<String> titleList = new ArrayList<>();
    private List<Integer> numberList = new ArrayList<>();
}
