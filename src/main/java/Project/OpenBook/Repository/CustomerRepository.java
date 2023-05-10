package Project.OpenBook.Repository;

import Project.OpenBook.Domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    public Optional<Customer> findByNickName(String nickName);
}
