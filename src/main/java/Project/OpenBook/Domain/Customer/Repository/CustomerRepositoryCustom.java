package Project.OpenBook.Domain.Customer.Repository;

import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryCustom {

    public Optional<Customer> queryCustomer(String oAuthId, String provider);

    public List<Customer> queryCustomersNotValidated();

    public Optional<Customer> queryCustomer(String code);

}
