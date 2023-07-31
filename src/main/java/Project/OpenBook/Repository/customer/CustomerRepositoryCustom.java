package Project.OpenBook.Repository.customer;

import Project.OpenBook.Domain.Customer;

import java.util.Optional;

public interface CustomerRepositoryCustom {

    public Optional<Customer> queryCustomer(String oAuthId, String provider);

    public Optional<Customer> queryCustomer(String code);
}
