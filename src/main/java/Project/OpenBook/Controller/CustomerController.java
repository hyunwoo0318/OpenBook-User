package Project.OpenBook.Controller;

import Project.OpenBook.Service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    //TODO : 로그인, 회원가입 구현
}
