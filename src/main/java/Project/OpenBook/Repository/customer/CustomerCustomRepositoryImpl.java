package Project.OpenBook.Repository.customer;

import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Domain.QCustomer;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static Project.OpenBook.Domain.QCustomer.customer;

@Repository
@RequiredArgsConstructor
public class CustomerCustomRepositoryImpl implements CustomerCustomRepository{

    private final JPAQueryFactory queryFactory;
    @Override
    public Customer queryCustomer(String oAuthId, String provider) {
        return queryFactory.selectFrom(customer)
                .where(customer.oAuthId.eq(oAuthId))
                .where(customer.provider.eq(provider))
                .fetchOne();
    }

    @Override
    public Customer queryCustomer(String code) {
        return queryFactory.selectFrom(customer)
                .where(customer.code.eq(code))
                .fetchOne();
    }
}
