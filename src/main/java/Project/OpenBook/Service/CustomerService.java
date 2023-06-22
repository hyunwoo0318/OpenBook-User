package Project.OpenBook.Service;

import Project.OpenBook.Dto.customer.CustomerCodeList;
import Project.OpenBook.Dto.customer.CustomerDetailDto;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Dto.customer.CustomerAddDetailDto;
import Project.OpenBook.Repository.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.CUSTOMER_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.DUP_NICKNAME;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer addDetails(Long customerId, CustomerAddDetailDto customerAddDetailDto) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomException(CUSTOMER_NOT_FOUND));
        String nickname = customerAddDetailDto.getNickname();
        checkDupNickname(nickname);

        customer.addDetails(nickname, customerAddDetailDto.getAge(), customerAddDetailDto.getExpertise());
        return customer;
    }

    private void checkDupNickname(String nickname) {
        customerRepository.findByNickName(nickname).ifPresent(c ->{
           throw new CustomException(DUP_NICKNAME);
        });
    }

    public CustomerCodeList queryCustomers() {
        List<String> codeList = customerRepository.findAll().stream().map(c -> c.getCode()).collect(Collectors.toList());
        return new CustomerCodeList(codeList);
    }

    public CustomerDetailDto queryCustomerDetail(String code) {
        Customer customer = customerRepository.queryCustomer(code);
        if (customer == null) {
            throw new CustomException(CUSTOMER_NOT_FOUND);
        }
        return CustomerDetailDto.builder()
                .age(customer.getAge())
                .log(null)
                .expertise(customer.getExpertise())
                .isSubscribed(customer.isSubscribed())
                .nickName(customer.getNickName())
                .build();
    }
}
