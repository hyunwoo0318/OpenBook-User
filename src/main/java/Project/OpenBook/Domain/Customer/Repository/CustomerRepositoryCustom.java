package Project.OpenBook.Domain.Customer.Repository;

import Project.OpenBook.Domain.Customer.Domain.Customer;

import java.util.Optional;

public interface CustomerRepositoryCustom {

    public Optional<Customer> queryCustomer(String oAuthId, String provider);

    public Optional<Customer> queryCustomer(String code);

}
