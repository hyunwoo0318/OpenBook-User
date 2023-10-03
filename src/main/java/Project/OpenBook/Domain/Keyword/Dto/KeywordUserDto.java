package Project.OpenBook.Domain.Keyword.Dto;

import Project.OpenBook.Domain.Topic.Service.dto.PrimaryDateDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordUserDto {
    private String name;
    private String comment;
    private String file;
    private List<PrimaryDateDto> extraDateList;
}