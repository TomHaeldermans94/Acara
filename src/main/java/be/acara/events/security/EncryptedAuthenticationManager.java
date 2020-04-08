package be.acara.events.security;

import be.acara.events.domain.User;
import be.acara.events.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EncryptedAuthenticationManager implements AuthenticationProvider {
    @Autowired
    private UserRepository userRepository;
    
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        final String name = authentication.getName();
        final String password = authentication.getCredentials().toString();
        
        User user = userRepository.findByUsername(name);
        if (user != null && user.getUsername().equals(name) && user.getPassword().equals(password)) {
            Set<GrantedAuthority> grantedAuthorities = user.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName()))
                    .collect(Collectors.toSet());
            final UserDetails userDetails = new org.springframework.security.core.userdetails.User(name, password, grantedAuthorities);
            return new UsernamePasswordAuthenticationToken(userDetails, password, grantedAuthorities);
        }
        return null;
    }
    
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
