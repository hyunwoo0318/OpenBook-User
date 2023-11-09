package Project.OpenBook.Domain.Keyword.Service.Dto;

import Project.OpenBook.Domain.Topic.Service.dto.PrimaryDateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordUserDto {
    private String name;
    private String comment;
    private String file;
    private String dateComment;
    private Integer number;
    private List<PrimaryDateDto> extraDateList;
}