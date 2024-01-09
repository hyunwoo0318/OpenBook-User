package Project.OpenBook.Domain.Customer.Service;

import static Project.OpenBook.Constants.ErrorCode.LOGIN_FAIL;
import static Project.OpenBook.Constants.ErrorCode.WRONG_PROVIDER_NAME;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Constants.StateConst;
import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
import Project.OpenBook.Domain.ExamQuestion.Repo.ExamQuestionRepository;
import Project.OpenBook.Domain.JJH.JJHContent.JJHContentRepository;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgress;
import Project.OpenBook.Domain.JJH.JJHContentProgress.JJHContentProgressRepository;
import Project.OpenBook.Domain.JJH.JJHList.JJHListRepository;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgress;
import Project.OpenBook.Domain.JJH.JJHListProgress.JJHListProgressRepository;
import Project.OpenBook.Domain.Keyword.Repository.KeywordRepository;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Domain.ExamQuestionLearningRecord;
import Project.OpenBook.Domain.LearningRecord.ExamQuestionLearningRecord.Repository.ExamQuestionLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Domain.KeywordLearningRecord;
import Project.OpenBook.Domain.LearningRecord.KeywordLearningRecord.Repo.KeywordLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Domain.QuestionCategoryLearningRecord;
import Project.OpenBook.Domain.LearningRecord.QuestionCategoryLearningRecord.Repo.QuestionCategoryLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecord;
import Project.OpenBook.Domain.LearningRecord.RoundLearningRecord.RoundLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Domain.TimelineLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TimelineLearningRecord.Repo.TimelineLearningRecordRepository;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Domain.TopicLearningRecord;
import Project.OpenBook.Domain.LearningRecord.TopicLearningRecord.Repo.TopicLearningRecordRepository;
import Project.OpenBook.Domain.QuestionCategory.Repo.QuestionCategoryRepository;
import Project.OpenBook.Domain.Round.Repo.RoundRepository;
import Project.OpenBook.Domain.Timeline.Repo.TimelineRepository;
import Project.OpenBook.Domain.Topic.Repo.TopicRepository;
import Project.OpenBook.Handler.Exception.CustomException;
import Project.OpenBook.Jwt.TokenDto;
import Project.OpenBook.Jwt.TokenManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@RequiredArgsConstructor
public class CustomerService implements UserDetailsService {

  private final CustomerRepository customerRepository;

  private final JJHListRepository jjhListRepository;
  private final JJHContentRepository jjhContentRepository;
  private final KeywordRepository keywordRepository;
  private final TopicRepository topicRepository;
  private final QuestionCategoryRepository questionCategoryRepository;
  private final TimelineRepository timelineRepository;
  private final RoundRepository roundRepository;
  private final ExamQuestionRepository examQuestionRepository;

  private final JJHListProgressRepository jjhListProgressRepository;
  private final JJHContentProgressRepository jjhContentProgressRepository;
  private final KeywordLearningRecordRepository keywordLearningRecordRepository;
  private final TopicLearningRecordRepository topicLearningRecordRepository;
  private final QuestionCategoryLearningRecordRepository questionCategoryLearningRecordRepository;
  private final TimelineLearningRecordRepository timelineLearningRecordRepository;
  private final RoundLearningRecordRepository roundLearningRecordRepository;
  private final ExamQuestionLearningRecordRepository examQuestionLearningRecordRepository;

  private final WebClient.Builder webClientBuilder;
  private final TokenManager tokenManager;
  private final ObjectMapper objectMapper;

  @Transactional
  public void deleteCustomer(Customer customer) {
    // TODO : 보다 효과적인 방법 찾기
    //        jjhListProgressRepository.deleteAllByCustomer(customer);
    //        jjhContentProgressRepository.deleteAllByCustomer(customer);
    //        keywordLearningRecordRepository.deleteAllByCustomer(customer);
    //        topicLearningRecordRepository.deleteAllByCustomer(customer);
    //        timelineLearningRecordRepository.deleteAllByCustomer(customer);
    //        questionCategoryLearningRecordRepository.deleteAllByCustomer(customer);
    //        roundLearningRecordRepository.deleteAllByCustomer(customer);
    //        examQuestionLearningRecordRepository.deleteAllByCustomer(customer);
    customerRepository.delete(customer);
  }

  @Override
  public Customer loadUserByUsername(String username) throws UsernameNotFoundException {
    return customerRepository
        .findByNickName(username)
        .orElseThrow(() -> new CustomException(LOGIN_FAIL));
  }

  /** Oauth2 */
  @Transactional
  public TokenDto loginOauth2(String providerName, String code, String redirectUrl, String protocol)
      throws Exception {

    Oauth2Login oauth2LoginStrategy;
    oauth2LoginStrategy = getOauth2LoginStrategy(providerName);

    // 카카오 or 네이버 로그인 완료 후 해당 oauthId return
    String oauthId = oauth2LoginStrategy.login(code, redirectUrl, protocol);

    Customer customer;
    Optional<Customer> customerOptional = customerRepository.queryCustomer(oauthId, providerName);
    if (customerOptional.isEmpty()) {
      // 회원가입이 되어있지 않음 -> 회원가입 시킴
      List<Customer> customerList = customerRepository.queryCustomersNotValidated();
      if (customerList.isEmpty()) {
        customer =
            Customer.builder()
                .oAuthId(oauthId)
                .provider(providerName)
                .roles(Role.USER)
                .isNew(true)
                .nickName(UUID.randomUUID().toString().substring(0, 8))
                .build();
        customerRepository.save(customer);

        // 단원전체진도, 단원섹션별 진도, 주제학습 레코드 생성
        initCustomerData(customer);
      } else {
        customer = customerList.get(0);
        customer.setInfo(
            UUID.randomUUID().toString().substring(0, 8), Role.USER, providerName, oauthId);
      }

    } else {
      customer = customerOptional.get();
    }

    return tokenManager.generateToken(customer);
  }

  private Oauth2Login getOauth2LoginStrategy(String providerName) {
    if (providerName.equals("kakao")) {
      return new KakaoLogin(webClientBuilder, objectMapper);
    } else if (providerName.equals("naver")) {
      return new NaverLogin(webClientBuilder, objectMapper);
    } else {
      throw new CustomException(WRONG_PROVIDER_NAME);
    }
  }

  public void initCustomerData(Customer customer) {

    // TODO : 비동기방식 사용해보기

    // 1. 정주행 리스트 progress
    initJJHListProgress(customer);

    // 2. 정주행 콘텐츠 progress
    initJJHContentProgress(customer);

    // 3. keyword 학습정도
    initKeywordLearningHistory(customer);

    // 4. topic 학습정도
    initTopicLearningHistory(customer);

    // 5. questionCategory 학습정도
    initQuestionCategoryLearningHistory(customer);

    // 6. timeline 학습정도
    initTimelineLearningHistory(customer);

    // 7. examQuestion 학습정도
    initExamQuestionLearningHistory(customer);

    // 8. round 학습정도
    initRoundLearningHistory(customer);
  }

  private void initRoundLearningHistory(Customer customer) {
    List<RoundLearningRecord> recordList =
        roundRepository.findAll().stream()
            .map(r -> new RoundLearningRecord(r, customer))
            .collect(Collectors.toList());
    roundLearningRecordRepository.saveAll(recordList);
  }

  private void initJJHListProgress(Customer customer) {
    List<JJHListProgress> jjhListProgressList =
        jjhListRepository.findAll().stream()
            .map(
                jl -> {
                  return jl.getNumber() == 1
                      ? new JJHListProgress(customer, jl, StateConst.IN_PROGRESS)
                      : new JJHListProgress(customer, jl);
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
    List<JJHContentProgress> jjhContentProgressList =
        jjhContentRepository.findAll().stream()
            .map(
                jc -> {
                  return jc.getNumber() == 1
                      ? new JJHContentProgress(customer, jc, StateConst.IN_PROGRESS)
                      : new JJHContentProgress(customer, jc);
                })
            .collect(Collectors.toList());

    jjhContentProgressRepository.saveAll(jjhContentProgressList);
  }

  private void initKeywordLearningHistory(Customer customer) {
    List<KeywordLearningRecord> recordList =
        keywordRepository.findAll().stream()
            .map(k -> new KeywordLearningRecord(k, customer))
            .collect(Collectors.toList());

    keywordLearningRecordRepository.saveAll(recordList);
  }

  private void initTopicLearningHistory(Customer customer) {
    List<TopicLearningRecord> recordList =
        topicRepository.findAll().stream()
            .map(t -> new TopicLearningRecord(t, customer))
            .collect(Collectors.toList());

    topicLearningRecordRepository.saveAll(recordList);
  }

  private void initQuestionCategoryLearningHistory(Customer customer) {
    List<QuestionCategoryLearningRecord> recordList =
        questionCategoryRepository.findAll().stream()
            .map(qc -> new QuestionCategoryLearningRecord(qc, customer))
            .collect(Collectors.toList());

    questionCategoryLearningRecordRepository.saveAll(recordList);
  }

  private void initTimelineLearningHistory(Customer customer) {
    List<TimelineLearningRecord> recordList =
        timelineRepository.findAll().stream()
            .map(tl -> new TimelineLearningRecord(tl, customer))
            .collect(Collectors.toList());

    timelineLearningRecordRepository.saveAll(recordList);
  }

  private void initExamQuestionLearningHistory(Customer customer) {
    List<ExamQuestionLearningRecord> recordList =
        examQuestionRepository.findAll().stream()
            .map(r -> new ExamQuestionLearningRecord(customer, r))
            .collect(Collectors.toList());

    examQuestionLearningRecordRepository.saveAll(recordList);
  }

  @Transactional
  public void resetCustomerRecord(Customer customer) {
    // 전체 초기화 로직
    // 1. 정주행 리스트 progress
    resetJJHListProgress(customer);

    // 2. 정주행 콘텐츠 progress
    resetJJHContentProgress(customer);

    // 3. keyword 학습정도
    resetKeywordLearningHistory(customer);

    // 4. topic 학습정도
    resetTopicLearningHistory(customer);

    // 5. questionCategory 학습정도
    resetQuestionCategoryLearningHistory(customer);

    // 6. timeline 학습정도
    resetTimelineLearningHistory(customer);

    // 7. examQuestion 학습정도
    resetExamQuestionLearningHistory(customer);

    // 8. round 학습정도
    resetRoundLearningHistory(customer);
  }

  private void resetRoundLearningHistory(Customer customer) {
    roundLearningRecordRepository.findAllByCustomer(customer).forEach(RoundLearningRecord::reset);
  }

  private void resetExamQuestionLearningHistory(Customer customer) {
    examQuestionLearningRecordRepository
        .findAllByCustomer(customer)
        .forEach(ExamQuestionLearningRecord::reset);
  }

  private void resetTimelineLearningHistory(Customer customer) {
    timelineLearningRecordRepository
        .findAllByCustomer(customer)
        .forEach(TimelineLearningRecord::reset);
  }

  private void resetQuestionCategoryLearningHistory(Customer customer) {
    questionCategoryLearningRecordRepository
        .findAllByCustomer(customer)
        .forEach(QuestionCategoryLearningRecord::reset);
  }

  private void resetTopicLearningHistory(Customer customer) {
    topicLearningRecordRepository.findAllByCustomer(customer).forEach(TopicLearningRecord::reset);
  }

  private void resetKeywordLearningHistory(Customer customer) {
    keywordLearningRecordRepository
        .findAllByCustomer(customer)
        .forEach(KeywordLearningRecord::reset);
  }

  private void resetJJHContentProgress(Customer customer) {
    jjhContentProgressRepository.findAllByCustomer(customer).forEach(JJHContentProgress::reset);
  }

  private void resetJJHListProgress(Customer customer) {
    jjhListProgressRepository.findAllByCustomer(customer).forEach(JJHListProgress::reset);
  }

  @Transactional
  public void isPolicyAgreed(Customer customer) {
    Customer newCustomer = customer.updateIsNew(false);
    customerRepository.save(newCustomer);
  }

  //    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Seoul")
  //    @Transactional
  //    public void makePreparedCustomer() {
  //        List<Customer> preparedCustomerList = customerRepository.queryCustomersNotValidated();
  //        int customerSize = preparedCustomerList.size();
  //        for (int i = customerSize; i <= 50; i++) {
  //            Customer customer = new Customer("mock" + i);
  //            customerRepository.save(customer);
  //            initCustomerData(customer);
  //        }
  //    }

}
