package Project.OpenBook.Jwt;

import Project.OpenBook.Utils.CustomException;
import Project.OpenBook.Domain.Customer;
import Project.OpenBook.Repository.customer.CustomerRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static Project.OpenBook.Constants.ErrorCode.*;

@Component
public class TokenManager {
    private Key key;
    private Date tokenExpire;

    private CustomerRepository customerRepository;

    public TokenManager(@Value("${jwt.secret}") String secretKey, CustomerRepository customerRepository){
        byte[] bytes = Base64Utils.decodeFromString(secretKey);
        this.key = Keys.hmacShaKeyFor(bytes);
        this.customerRepository = customerRepository;
    }

    public TokenDto generateToken(Authentication authentication, long id) {
        checkCustomer(id);
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        tokenExpire = new Date(now + JwtConst.TOKEN_EXPIRED_TIME);

        String accessToken = Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("auth", authorities) // claim -> jwt body custom claim
                .setIssuer(JwtConst.TOKEN_ISSUER)
                .setExpiration(tokenExpire) // 일반적인 claim에 대해서는 setter를 제공함
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(String.valueOf(id))
                .setExpiration(tokenExpire) // 표준에서 refresh token과 access token을 유효기간을 같이 설정하도록 권고
                .signWith(key,SignatureAlgorithm.HS256)
                .compact();

        return TokenDto.builder()
                .type("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public TokenDto generateToken(String refreshToken) {
        Claims claims = parseClaim(refreshToken);
        long id = Long.parseLong(claims.getSubject());

        Customer customer = checkCustomer(id);

        Authentication authentication = (Authentication) customer;
        return generateToken(authentication, customer.getId());
    }

    public Authentication getAuthorities(String accessToken){
        Claims claim = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();

        long id = Long.parseLong(claim.getSubject());
        Customer findCustomer = checkCustomer(id);
        if(claim.get("auth") == null){
            throw new CustomException(NOT_AUTHORIZED);
        }

        Collection<? extends GrantedAuthority> authorities = findCustomer.getAuthorities();
        UserDetails userDetails = new User(claim.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    public void validateToken(String accessToken){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
        } catch (Exception e) {
            throw new CustomException(INVALID_TOKEN);
        }
    }

    public Claims parseClaim(String accessToken){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch(ExpiredJwtException e){
            throw new CustomException(INVALID_TOKEN);
        }
    }

    public String resolveRequest(HttpServletRequest req){
        String token = req.getHeader("Authorization");
        if (token == null) {
            return null;
        }
        if(!token.startsWith("Bearer")){
            return null;
        }
        return token.substring(7);
    }

    public void invalidateToken(String refreshToken) {
        Claims claims = parseClaim(refreshToken);
        claims.setExpiration(null);
    }

    private Customer checkCustomer(Long customerId) {
        return customerRepository.findById(customerId).orElseThrow(() -> {
            throw new CustomException(CUSTOMER_NOT_FOUND);
        });
    }
}