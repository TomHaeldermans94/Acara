package be.acara.events.security;

import be.acara.events.domain.User;
import be.acara.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class EncryptedAuthenticationManager implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final String password = authentication.getCredentials().toString();
        
        User user = userRepository.findByUsername(name);
        if (user != null && user.getUsername().equals(name) && user.getPassword().equals(password)) {
            final UserDetails userDetails = new org.springframework.security.core.userdetails.User(name, password, Collections.emptyList());
            final Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, password, Collections.emptyList());
            return auth;
        }
        return null;
    }
    
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
