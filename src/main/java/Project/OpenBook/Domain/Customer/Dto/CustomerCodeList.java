package Project.OpenBook.Domain.Customer.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerCodeList {
    private List<String> customerCodeList;
}
