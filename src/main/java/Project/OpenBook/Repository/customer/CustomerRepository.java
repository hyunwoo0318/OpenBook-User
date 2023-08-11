package Project.OpenBook.Repository.customer;

import Project.OpenBook.Domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, CustomerRepositoryCustom {

    public Optional<Customer> findByNickName(String nickName);

    public Optional<Customer> findByLoginId(String loginId);
}
