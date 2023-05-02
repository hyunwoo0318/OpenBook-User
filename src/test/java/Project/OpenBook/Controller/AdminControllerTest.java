package Project.OpenBook.Controller;

import Project.OpenBook.Dto.admin.AdminDto;
import Project.OpenBook.Dto.error.ErrorDto;
import Project.OpenBook.Repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
class AdminControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    AdminRepository adminRepository;

    private final String prefix = "http://localhost:";
    private final String surfix = "/admin/login";

    String URL;

    @BeforeEach
    public void deleteAll() {
        URL = prefix + port + surfix;
        restTemplate = restTemplate.withBasicAuth("admin1", "admin1");
        restTemplate.getRestTemplate().setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    @DisplayName("성공적인 로그인 - POST /admin/login")
    @Test
    public void adminLoginSuccess() {
        AdminDto adminDto = new AdminDto("admin1", "admin1");
        ResponseEntity<Void> response = restTemplate.postForEntity(URL, adminDto, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getHeaders().get("Set-Cookie")).isNotNull();
    }

    @DisplayName("로그인 실패 - POST /admin/login")
    @Test
    public void adminLoginFail() {
        AdminDto wrongDto1 = new AdminDto();
        wrongDto1.setLoginId("admin1");
        AdminDto wrongDto2 = new AdminDto();
        wrongDto2.setPassword("admin1");
        AdminDto wrongDto3 = new AdminDto("admin1", "wrong");

        ResponseEntity<List<ErrorDto>> response1 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto1), new ParameterizedTypeReference<List<ErrorDto>>() {
        });
        ResponseEntity<List<ErrorDto>> response2 = restTemplate.exchange(URL, HttpMethod.POST, new HttpEntity<>(wrongDto2), new ParameterizedTypeReference<List<ErrorDto>>() {
        });

        ResponseEntity<Void> response3 = restTemplate.postForEntity(URL, wrongDto3, Void.class);

        assertThat(response1.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response1.getBody().get(0)).isEqualTo(new ErrorDto("password", "비밀번호를 입력해주세요."));

        assertThat(response2.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response2.getBody().get(0)).isEqualTo(new ErrorDto("loginId", "로그인 아이디를 입력해주세요."));

        assertThat(response3.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}