package Project.OpenBook.Repository.customer;

import Project.OpenBook.Domain.Customer;

public interface CustomerCustomRepository {

    public Customer queryCustomer(String oAuthId, String provider);
}
