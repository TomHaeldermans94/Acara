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
    
    /**
     * Overrides the default implementation of authenticate. This implementation will check if the found user and
     * provided user's username and passwords are exactly the same.
     *
     * @param authentication the authentication to verify
     * @return an {@link UsernamePasswordAuthenticationToken}
     */
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
    
    /**
     * Whether the authentication provider should accept the specified token
     *
     * @param aClass the token class
     * @return true if it should be handled
     */
    @Override
    public boolean supports(Class<?> aClass) {
        return aClass.equals(UsernamePasswordAuthenticationToken.class);
    }
}
