//package Project.OpenBook.Repository;
//
//import Project.OpenBook.Config.TestQueryDslConfig;
//import Project.OpenBook.Domain.Customer.Repository.CustomerRepository;
//import org.junit.jupiter.api.DisplayName;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@Import(TestQueryDslConfig.class)
//@AutoConfigureTestDatabase(replace= AutoConfigureTestDatabase.Replace.NONE)
//@DisplayName("CustomerRepository class")
//public class CustomerRepositoryTest {
//
//    @Autowired
//    CustomerRepository customerRepository;
//
////    @Nested
////    @DisplayName("queryCustomer(oauthId, provider) 메서드는")
////    public class queryCustomerOauthIdTest{
////
////        @Nested
////        @DisplayName("입력한 oauthId, provider과 일치하는 회원이 존재하는 경우")
////        public class existCustomer{
////
////            @Test
////            @DisplayName("해당 회원을 Optional로 감싸서 리턴한다.")
////            public void returnCustomerOptional() {
////                //given
////                String providerName = "kakao";
////                String oauthId = "a123";
////                Customer customer = new Customer("nickname1", 23, 0, Role.USER, providerName, oauthId, false);
////                customerRepository.save(customer);
////
////                //when
////                Optional<Customer> customerOptional = customerRepository.queryCustomer(providerName, oauthId);
////
////                //then
////                assertThat(customerOptional.get()).isEqualTo(customer);
////
////                customerRepository.deleteAllInBatch();
////            }
////        }
////
////        @Nested
////        @DisplayName("입력한 oauthId, provider과 일치하는 회원이 존재하지 않는 경우")
////        public class notExistCustomer {
////
////            @Test
////            @DisplayName("null값을 Optional로 감싸서 리턴한다.")
////            public void returnNullOptional(){
////                //given
////
////                //when
////                Optional<Customer> customerOptional = customerRepository.queryCustomer("kakao", "a123");
////
////                //then
////                assertThat(customerOptional.isEmpty()).isTrue();
////            }
////        }
////    }
//
////    @Nested
////    @DisplayName("queryCustomer(code) 메서드는")
////    public class queryCustomerCodeTest{
////
////        @Nested
////        @DisplayName("입력한 code와 일치하는 회원이 존재하는 경우")
////        public class existCustomer{
////
////            @Test
////            @DisplayName("해당 회원을 Optional로 감싸서 리턴한다.")
////            public void returnCustomerOptional() {
////                //given
////                String providerName = "kakao";
////                String oauthId = "a123";
////                Customer customer = new Customer("nickname1", 23, 0, Role.USER, providerName, oauthId, false);
////                customerRepository.save(customer);
////
////                //when
////                Optional<Customer> customerOptional = customerRepository.queryCustomer(providerName, oauthId);
////
////                //then
////                assertThat(customerOptional.get()).isEqualTo(customer);
////
////                customerRepository.deleteAllInBatch();
////            }
////        }
////
////        @Nested
////        @DisplayName("입력한 oauthId, provider과 일치하는 회원이 존재하지 않는 경우")
////        public class notExistCustomer {
////
////            @Test
////            @DisplayName("null값을 Optional로 감싸서 리턴한다.")
////            public void returnNullOptional(){
////                //given
////
////                //when
////                Optional<Customer> customerOptional = customerRepository.queryCustomer("kakao", "a123");
////
////                //then
////                assertThat(customerOptional.isEmpty()).isTrue();
////            }
////        }
////    }
//}
