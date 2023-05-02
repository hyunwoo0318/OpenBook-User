package Project.OpenBook.Service;

import Project.OpenBook.Constants.Role;
import Project.OpenBook.Domain.Admin;
import Project.OpenBook.Repository.AdminRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @DisplayName("정상적인 회원가입")
    @Test
    public void normalRegisterAdmin(){
        String loginId = "adminLoginId123";
        String password = "adminPassword123";
        Admin registerAdmin = adminService.registerAdmin(loginId, password);

        assertThat(registerAdmin.getLoginId()).isEqualTo(loginId);
        assertThat(registerAdmin.getPassword()).isEqualTo(passwordEncoder.encode(password));
        assertThat(registerAdmin.getRole()).isEqualTo(Role.ADMIN);
    }

}