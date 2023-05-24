package Project.OpenBook.Jwt;

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
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

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

        Optional<Customer> customerOptional = customerRepository.findById(id);
        if (customerOptional.isEmpty()) {
            return null;
        }
        Customer customer = customerOptional.get();

        Authentication authentication = (Authentication) customer;
        return generateToken(authentication, customer.getId());
    }

    public Authentication getAuthorities(String accessToken) throws Exception{
        Claims claim = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();

        long id = Long.parseLong(claim.getSubject());
        Optional<Customer> findCustomer = customerRepository.findById(id);
        if(findCustomer.isEmpty()){
            throw new RuntimeException("유효하지 않은 사용자입니다.");
        }
        if(claim.get("auth") == null){
            throw new RuntimeException("허용되지 않은 사용자입니다.");
        }

        Collection<? extends GrantedAuthority> authorities = findCustomer.get().getAuthorities();
        UserDetails userDetails = new User(claim.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, "", authorities);
    }

    public boolean validateToken(String accessToken){
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new RuntimeException("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            throw new RuntimeException("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("JWT claims string is empty.", e);
        }
    }

    public Claims parseClaim(String accessToken){
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch(ExpiredJwtException e){
            return e.getClaims();
        }
    }

    public String resolveRequest(HttpServletRequest req){
        String token = req.getHeader("Authorization");
        if(StringUtils.hasText(token) && token.startsWith("Bearer")){
            return token.substring(7);
        }
        return null;
    }

    public void invalidateToken(String refreshToken) {
        Claims claims = parseClaim(refreshToken);
        claims.setExpiration(null);
    }
}