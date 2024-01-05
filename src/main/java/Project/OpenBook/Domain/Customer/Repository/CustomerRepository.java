package Project.OpenBook.Domain.Customer.Repository;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long>,
    CustomerRepositoryCustom {

    public Optional<Customer> findByNickName(String nickName);

}
