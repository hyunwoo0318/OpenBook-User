package Project.OpenBook.Utils;

import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Repository.customer.CustomerRepository;
import Project.OpenBook.Service.CustomerService;
import com.amazonaws.services.kms.model.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

@RequiredArgsConstructor
public class WithAccountSecurityContextFactory implements WithSecurityContextFactory<WithAccount> {

    private final CustomerService customerService;
    @Override
    public SecurityContext createSecurityContext(WithAccount annotation) {
        Customer customer = customerService.queryCustomer(annotation.value()).orElseThrow(() -> {
            throw new NotFoundException("사용자 없음");
        });

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customer, "", customer.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);

        return context;
    }
}
