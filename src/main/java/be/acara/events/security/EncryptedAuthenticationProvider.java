package be.acara.events.security;

import be.acara.events.domain.User;
import be.acara.events.service.UserService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class EncryptedAuthenticationProvider implements AuthenticationProvider {
    private final UserService userService;
    
    public EncryptedAuthenticationProvider(UserService userService) {
        this.userService = userService;
    }
    
    
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) {
        final String name = authentication.getName();
        final String password = authentication.getCredentials().toString();
        
        User user = userService.findByUsername(name);
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
