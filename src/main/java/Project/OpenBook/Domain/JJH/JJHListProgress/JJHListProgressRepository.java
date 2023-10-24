package Project.OpenBook.Domain.JJH.JJHListProgress;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JJHListProgressRepository extends JpaRepository<JJHListProgress, Long>, JJHListProgressRepositoryCustom {

    public void deleteAllByCustomer(Customer customer);

    public Optional<JJHListProgress> findByCustomerAndJjhList(Customer customer, JJHList jjhList);
}
