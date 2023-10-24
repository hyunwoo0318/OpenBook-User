package Project.OpenBook.Domain.Customer.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Constants.ProgressConst;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContentRepository;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgress;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgressRepository;
import Project.OpenBook.Domain.JJH.JJHList.JJHListRepository;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgress;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgressRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Jwt.TokenDto;
import Project.OpenBook.Jwt.TokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.LOGIN_FAIL;
import static Project.OpenBook.Constants.ErrorCode.WRONG_PROVIDER_NAME;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final JJHListRepository jjhListRepository;
    private final JJHListProgressRepository jjhListProgressRepository;

    private final JJHContentProgressRepository jjhContentProgressRepository;
    private final JJHContentRepository jjhContentRepository;



    private final AuthenticationManagerBuilder authenticationManager;
    private final WebClient.Builder webClientBuilder;
    private final TokenManager tokenManager;
    private final ObjectMapper objectMapper;

//    public Customer addDetails(Long customerId, CustomerAddDetailDto customerAddDetailDto) {
//        Customer customer = customerRepository.findById(customerId).orElseThrow(() -> new CustomException(CUSTOMER_NOT_FOUND));
//        String nickname = customerAddDetailDto.getNickname();
//        checkDupNickname(nickname);
//
//        customer.addDetails(nickname, customerAddDetailDto.getAge(), customerAddDetailDto.getExpertise());
//        return customer;
//    }

//    private void checkDupNickname(String nickname) {
//        customerRepository.findByNickName(nickname).ifPresent(c ->{
//           throw new CustomException(DUP_NICKNAME);
//        });
//    }
//
//    public CustomerCodeList queryCustomers() {
//        List<String> codeList = customerRepository.findAll().stream().map(c -> c.getCode()).collect(Collectors.toList());
//        return new CustomerCodeList(codeList);
//    }
//
//    public CustomerDetailDto queryCustomerDetail(String code) {
//        Customer customer = customerRepository.queryCustomer(code)
//                .orElseThrow(() -> {
//                    throw new CustomException(CUSTOMER_NOT_FOUND);
//                });
//        return CustomerDetailDto.builder()
//                .age(customer.getAge())
//                .log(null)
//                .expertise(customer.getExpertise())
//                .isSubscribed(customer.isSubscribed())
//                .nickName(customer.getNickName())
//                .build();
//    }

    @Transactional
    public void deleteCustomer(Customer customer) {
        customerRepository.delete(customer);
    }


    /**
     * 관리자
     */
    @Transactional
    public TokenDto loginAdmin(String loginId, String password){
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(loginId, password);
        Authentication authentication = authenticationManager.getObject().authenticate(upToken);

        if(!authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        Customer customer = (Customer) authentication.getPrincipal();
        TokenDto tokenDto = tokenManager.generateToken(customer);
        return tokenDto;
    }

    @Override
    public Customer loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("IN LOAD BY USERNAME!!!!!!!!!!!!! +++++++++++++++");
        return customerRepository.findByNickName(username).orElseThrow(() -> {
            throw new CustomException(LOGIN_FAIL);
        });
    }


    /**
     * Oauth2
     */
    @Transactional
    public TokenDto loginOauth2(String providerName, String code, String redirectUrl) throws Exception{

        Oauth2Login oauth2LoginStrategy;
        oauth2LoginStrategy = getOauth2LoginStrategy(providerName);

        //카카오 or 네이버 로그인 완료 후 해당 oauthId return
        String oauthId = oauth2LoginStrategy.login(code,redirectUrl);

        Customer customer;
        Optional<Customer> customerOptional = customerRepository.queryCustomer(oauthId, providerName);
        if (customerOptional.isEmpty()) {
            //회원가입이 되어있지 않음 -> 회원가입 시킴
            customer = Customer.builder()
                    .oAuthId(oauthId)
                    .provider(providerName)
                    .roles(Role.USER)
                    .nickName(UUID.randomUUID().toString())
                    .build();
            customerRepository.save(customer);

            //단원전체진도, 단원섹션별 진도, 주제학습 레코드 생성
            initJJHListProgress(customer);
            initJJHContentProgress(customer);
        }else{
            customer = customerOptional.get();
        }

        TokenDto tokenDto = tokenManager.generateToken(customer);
        return tokenDto;
    }

    private Oauth2Login getOauth2LoginStrategy(String providerName) {
        if(providerName.equals("kakao")){
            return new KakaoLogin(webClientBuilder, objectMapper);
        }else if(providerName.equals("naver")){
            return new NaverLogin(webClientBuilder, objectMapper);
        }else{
            throw new CustomException(WRONG_PROVIDER_NAME);
        }
    }

    private void initJJHListProgress(Customer customer) {
        List<JJHListProgress> jjhListProgressList = jjhListRepository.findAll().stream()
                .map(jl -> {
                    return jl.getNumber() == 1 ?
                            new JJHListProgress(customer, jl, StateConst.OPEN, ProgressConst.NOT_STARTED) :
                            new JJHListProgress(customer, jl);
                })
                .collect(Collectors.toList());

        try {
            jjhListProgressRepository.saveAll(jjhListProgressList);
        } catch (Exception e) {
            jjhListProgressRepository.deleteAllByCustomer(customer);
            jjhListProgressRepository.saveAll(jjhListProgressList);
        }
    }

    private void initJJHContentProgress(Customer customer) {
        List<JJHContentProgress> jjhContentProgressList = jjhContentRepository.findAll().stream()
                .map(jc -> {
                    return jc.getNumber() == 1 ?
                            new JJHContentProgress(customer, jc, StateConst.OPEN) :
                            new JJHContentProgress(customer, jc);
                })
                .collect(Collectors.toList());

        try {
            jjhContentProgressRepository.saveAll(jjhContentProgressList);
        } catch (Exception e) {
            jjhContentProgressRepository.deleteAllByCustomer(customer);
            jjhContentProgressRepository.saveAll(jjhContentProgressList);
        }
    }

}
