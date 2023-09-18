package Project.OpenBook.Domain.Keyword.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordUserDto {
    private String name;
    private String comment;
    private String file;
}