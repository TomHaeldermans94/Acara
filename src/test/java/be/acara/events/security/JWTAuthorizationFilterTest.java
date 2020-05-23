package be.acara.events.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static be.acara.events.security.SecurityConstants.HEADER_STRING;
import static be.acara.events.security.SecurityConstants.TOKEN_PREFIX;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTAuthorizationFilterTest {
    
    @Mock
    private AuthenticationManager authenticationManager;
    
    @InjectMocks
    @Spy
    private JWTAuthorizationFilter authorizationFilter;
    
    @BeforeEach
    void setUp() {
        authorizationFilter = new JWTAuthorizationFilter(authenticationManager);
    }
    
    @Test
    void doFilterInternal_hasNullValueInGetAuthentication() throws IOException, ServletException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);
        
        when(request.getHeader(HEADER_STRING)).thenReturn(TOKEN_PREFIX + " some jwt stuff", null);
        
        authorizationFilter.doFilterInternal(request, response, chain);
        
        verify(request, times(2)).getHeader(HEADER_STRING);
        verify(chain, times(1)).doFilter(request, response);
    }
}
