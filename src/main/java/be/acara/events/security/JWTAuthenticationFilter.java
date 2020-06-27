package be.acara.events.security;

import be.acara.events.domain.User;
import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static be.acara.events.security.SecurityConstants.*;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

/**
 * This class manages the authentication and assigning of Spring Security tokens to a session.
 */
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    
    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    /**
     * Reads the credentials from the request and tries to authenticate it
     *
     * @param request  Spring-injected HttpServletRequest
     * @param response Spring-injected HttpServletResponse
     * @return the Authentication result
     */
    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        User creds = new ObjectMapper().readValue(request.getInputStream(), User.class);
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getUsername(),
                        creds.getPassword(),
                        new ArrayList<>()
                )
        );
    }
    
    /**
     * Creates a JWT on a successful authentication. It will add the JWT to the Authorization header.
     *
     * @param request    Spring-injected HttpServletRequest
     * @param response   Spring-injected HttpServletResponse
     * @param chain      Spring-injected FilterChain
     * @param authResult the presumably successful authentication
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) {
        List<String> roles = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        final String token = JWT.create()
                .withSubject(((User) authResult.getPrincipal()).getUsername())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .withClaim("roles", roles)
                .sign(HMAC512(SECRET.getBytes()));
        response.addHeader(HEADER_STRING, TOKEN_PREFIX + token);
    }
}
