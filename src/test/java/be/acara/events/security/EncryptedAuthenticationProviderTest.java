package be.acara.events.security;

import be.acara.events.domain.User;
import be.acara.events.service.UserService;
import be.acara.events.testutil.UserUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EncryptedAuthenticationProviderTest {
    @Mock
    private UserService userService;
    private EncryptedAuthenticationProvider authenticationProvider;
    
    @BeforeEach
    void setUp() {
        authenticationProvider = new EncryptedAuthenticationProvider(userService);
    }
    
    @Test
    void authenticate() {
        Authentication authentication = mock(Authentication.class);
        User user = UserUtil.firstUser();
        
        when(authentication.getName()).thenReturn(user.getUsername());
        when(authentication.getCredentials()).thenReturn(user.getPassword());
        when(userService.findByUsername(authentication.getName())).thenReturn(user);
        
        Authentication answer = authenticationProvider.authenticate(authentication);
        
        assertThat(answer).isNotNull();
        assertThat(answer).isInstanceOf(UsernamePasswordAuthenticationToken.class);
        
        UsernamePasswordAuthenticationToken answerToken = (UsernamePasswordAuthenticationToken) answer;
        
        assertThat(answerToken.getName()).isEqualTo(user.getUsername());
        assertThat(answerToken.getCredentials()).isEqualTo(user.getPassword());
        assertThat(answerToken.getAuthorities()).containsAll(user.getAuthorities());
    }
}
