package Project.OpenBook.Domain.Keyword.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class KeywordDto {

    private String name;

    private String comment;
    private String file;

    private Long id;
}
