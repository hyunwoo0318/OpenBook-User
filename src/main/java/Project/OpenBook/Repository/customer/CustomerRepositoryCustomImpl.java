package Project.OpenBook.Repository.customer;

import Project.OpenBook.Domain.Customer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static Project.OpenBook.Domain.QCustomer.customer;

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
    public Optional<Customer> queryCustomer(String code) {
        Customer findCustomer = queryFactory.selectFrom(customer)
                .where(customer.code.eq(code))
                .fetchOne();
        return Optional.ofNullable(findCustomer);
    }


}
