package Project.OpenBook.Domain.JJH.JJHListProgress;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.JJH.JJHList.JJHList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JJHListProgressRepository extends JpaRepository<JJHListProgress, Long>,
    JJHListProgressRepositoryCustom {

    public void deleteAllByCustomer(Customer customer);

    public List<JJHListProgress> findAllByCustomer(Customer customer);

    public Optional<JJHListProgress> findByCustomerAndJjhList(Customer customer, JJHList jjhList);
}
