package Project.OpenBook.Domain.JJH.JJHContentProgress;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JJHContentProgressRepository extends JpaRepository<JJHContentProgress, Long>,
    JJHContentProgressRepositoryCustom {

    public void deleteAllByCustomer(Customer customer);

    public List<JJHContentProgress> findAllByCustomer(Customer customer);


}
