package Project.OpenBook.Domain.Customer.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerNicknameDto {
    private String id;
    private Boolean isNew;
}
