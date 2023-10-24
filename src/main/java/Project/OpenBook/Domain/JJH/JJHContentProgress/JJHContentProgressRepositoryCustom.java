package Project.OpenBook.Domain.JJH.JJHContentProgress;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import Project.OpenBook.Domain.JJH.dto.TotalProgressDto;

import java.util.List;
import java.util.Optional;

public interface JJHContentProgressRepositoryCustom {

    public List<JJHContentProgress> queryJJHContentProgressForCustomer(Customer customer, Integer number);

    public Optional<JJHContentProgress> queryJJHContentProgressWithJJHContent(Customer customer, Integer number);

    public TotalProgressDto queryTotalProgressDto(Customer customer);
}
