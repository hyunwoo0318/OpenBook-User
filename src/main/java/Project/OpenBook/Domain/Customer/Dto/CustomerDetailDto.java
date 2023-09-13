package Project.OpenBook.Domain.Customer.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CustomerDetailDto {

    private String nickName;

    private Integer expertise;

    private Integer age;

    private String log;

    private Boolean isSubscribed;

    @Builder
    public CustomerDetailDto(String nickName, Integer expertise, Integer age, String log, Boolean isSubscribed) {
        this.nickName = nickName;
        this.expertise = expertise;
        this.age = age;
        this.log = log;
        this.isSubscribed = isSubscribed;
    }
}
