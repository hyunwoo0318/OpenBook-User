package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.CustomException;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Dto.customer.CustomerDetailDto;
import Project.OpenBook.Repository.customer.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static Project.OpenBook.Constants.ErrorCode.CUSTOMER_NOT_FOUND;
import static Project.OpenBook.Constants.ErrorCode.DUP_NICKNAME;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    public Customer addDetails(Long customerId, CustomerDetailDto customerDetailDto) {
        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomException(CUSTOMER_NOT_FOUND));
        String nickname = customerDetailDto.getNickname();
        checkDupNickname(nickname);

        customer.addDetails(nickname, customerDetailDto.getAge(), customerDetailDto.getCurrentGrade());
        return customer;
    }

    private void checkDupNickname(String nickname) {
        customerRepository.findByNickName(nickname).ifPresent(c ->{
           throw new CustomException(DUP_NICKNAME);
        });
    }
}
