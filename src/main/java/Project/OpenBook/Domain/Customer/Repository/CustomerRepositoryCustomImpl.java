package Project.OpenBook.Domain.Customer.Repository;

import Project.OpenBook.Domain.Customer.Domain.Customer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static Project.OpenBook.Domain.Customer.Domain.QCustomer.customer;


@Repository
@RequiredArgsConstructor
public class CustomerRepositoryCustomImpl implements CustomerRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    @Override
    public Optional<Customer> queryCustomer(String oAuthId, String provider) {
        Customer findCustomer = queryFactory.selectFrom(customer)
                .where(customer.oAuthId.eq(oAuthId))
                .where(customer.provider.eq(provider))
                .fetchOne();
        return Optional.ofNullable(findCustomer);
    }

    @Override
    public List<Customer> queryCustomersNotValidated() {
        return queryFactory.selectFrom(customer)
                .where(customer.isValidated.isFalse())
                .fetch();
    }

    @Override
    public Optional<Customer> queryCustomer(String code) {
        Customer findCustomer = queryFactory.selectFrom(customer)
                .where(customer.code.eq(code))
                .fetchOne();
        return Optional.ofNullable(findCustomer);
    }


}
