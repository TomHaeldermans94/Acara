package be.acara.events.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static be.acara.events.security.SecurityConstants.*;
import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {
    
    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(HEADER_STRING);
        
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request,response);
            return;
        }
    
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
    
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
    
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader(HEADER_STRING);
        if (token != null) {
            DecodedJWT jwt = JWT.require(HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token.replace(TOKEN_PREFIX, ""));
            
            String user = jwt.getSubject();
            Map<String, Claim> claims = jwt.getClaims();
            Set<SimpleGrantedAuthority> roles = claims.get("roles").asList(String.class)
                    .stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toSet());
    
            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, roles);
            }
        }
        return null;
    }
}
