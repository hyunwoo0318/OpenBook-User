package Project.OpenBook.Service.Customer;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.ChapterSection;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Domain.TopicProgress;
import Project.OpenBook.Dto.customer.CustomerAddDetailDto;
import Project.OpenBook.Dto.customer.CustomerCodeList;
import Project.OpenBook.Dto.customer.CustomerDetailDto;
import Project.OpenBook.Jwt.TokenDto;
import Project.OpenBook.Jwt.TokenManager;
import Project.OpenBook.Repository.chapter.ChapterRepository;
import Project.OpenBook.Repository.chaptersection.ChapterSectionRepository;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Repository.topic.TopicRepository;
import Project.OpenBook.Repository.topicprogress.TopicProgressRepository;
import Project.OpenBook.Utils.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;
    private final TopicProgressRepository topicProgressRepository;
    private final ChapterSectionRepository chapterSectionRepository;
    private final AuthenticationManagerBuilder authenticationManager;
    private final WebClient.Builder webClientBuilder;
    private final TokenManager tokenManager;

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
        Customer customer = customerRepository.queryCustomer(code)
                .orElseThrow(() -> {
                    throw new CustomException(CUSTOMER_NOT_FOUND);
                });
        return CustomerDetailDto.builder()
                .age(customer.getAge())
                .log(null)
                .expertise(customer.getExpertise())
                .isSubscribed(customer.isSubscribed())
                .nickName(customer.getNickName())
                .build();
    }

    public void deleteCustomer(long customerId) {
        customerRepository.deleteById(customerId);
    }

    public Optional<Customer> queryCustomer(String nickName){
        return customerRepository.findByNickName(nickName);
    }

    /**
     * 관리자
     */
    public void loginAdmin(String loginId, String password){
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(loginId, password);
        Authentication authentication = authenticationManager.getObject().authenticate(upToken);

        if(!authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
    }

    @Override
    public Customer loadUserByUsername(String username) throws UsernameNotFoundException {
        return customerRepository.findByNickName(username).orElseThrow(() -> {
            throw new CustomException(LOGIN_FAIL);
        });
    }


    /**
     * Oauth2
     */
    public TokenDto loginOauth2(String providerName, String code) throws Exception{

        Oauth2Login oauth2LoginStrategy;
        oauth2LoginStrategy = getOauth2LoginStrategy(providerName);

        //카카오 or 네이버 로그인 완료 후 해당 oauthId return
        String oauthId = oauth2LoginStrategy.login(code);

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

            //단원학습, 주제학습 레코드 생성
            updateChapterProgress(customer);
            updateTopicProgress(customer);
        }else{
            customer = customerOptional.get();
        }

        TokenDto tokenDto = tokenManager.generateToken(customer);
        return tokenDto;
    }

    private Oauth2Login getOauth2LoginStrategy(String providerName) {
        if(providerName.equals("kakao")){
            return new KakaoLogin(webClientBuilder);
        }else if(providerName.equals("naver")){
            return new NaverLogin(webClientBuilder);
        }else{
            throw new CustomException(WRONG_PROVIDER_NAME);
        }
    }

    private void updateTopicProgress(Customer customer) {
        List<TopicProgress> topicProgressList = topicRepository.findAll().stream()
                .map(t -> new TopicProgress(customer, t))
                .collect(Collectors.toList());
        topicProgressRepository.saveAll(topicProgressList);
    }

    private void updateChapterProgress(Customer customer) {
        List<ChapterSection> chapterSectionList = chapterRepository.findAll().stream()
                .map(c -> new ChapterSection(customer, c))
                .collect(Collectors.toList());
        chapterSectionRepository.saveAll(chapterSectionList);
    }


}
