package be.acara.events.security;

import be.acara.events.domain.User;
import be.acara.events.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EncryptedAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository userRepository;
    
    public EncryptedAuthenticationProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) {
        final String name = authentication.getName();
        final String password = authentication.getCredentials().toString();
        
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (user.getUsername().equals(name) && user.getPassword().equals(password)) {
            return new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        }
        return null;
    }
    
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
