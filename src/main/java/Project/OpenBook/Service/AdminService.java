package Project.OpenBook.Service;

import Project.OpenBook.Constants.ErrorCode;
import Project.OpenBook.Constants.Role;
import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.Admin;
import Project.OpenBook.Repository.admin.AdminRepository;
import com.fasterxml.jackson.databind.exc.InvalidNullException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final AuthenticationManagerBuilder authenticationManager;
    private final PasswordEncoder passwordEncoder;


    /**
     * 관리자 회원가입 메서드
     * @param loginId -> 로그인아이디
     * @param password -> 비밀번호
     * @return 이미 가입이 되어있는 아이디로 회원가입을 시도할경우 null 리턴
     * @return 정상적인 경우는 가입된 Admin정보 리턴
     */

    public Admin registerAdmin(String loginId, String password) {
        Optional<Admin> optionalAdmin = adminRepository.findByLoginId(loginId);
        //중복된 아이디로 회원가입을 시도할경우
        if(optionalAdmin.isPresent()) return null;
        password = passwordEncoder.encode(password);
        Admin admin = Admin.builder()
                .loginId(loginId)
                .password(password)
                .role(Role.ADMIN)
                .build();
        adminRepository.save(admin);
        return admin;
    }

    public void login(String loginId, String password, HttpServletRequest request){
        UsernamePasswordAuthenticationToken upToken = new UsernamePasswordAuthenticationToken(loginId, password);
        Authentication authentication = authenticationManager.getObject().authenticate(upToken);

        if(!authentication.isAuthenticated()) {
            throw new CustomException(ErrorCode.LOGIN_FAIL);
        }
        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);
        request.getSession(true);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByLoginId(username).orElseThrow(() -> new UsernameNotFoundException("등록되지 않은 관리자입니다."));
        return User.builder()
                .username(admin.getUsername())
                .password(admin.getPassword())
                .authorities(admin.getAuthorities())
                .build();
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }
    }
}
