package com.fratoni.todolist.filter;

import com.fratoni.todolist.user.IUserRepository;
import com.fratoni.todolist.user.UserModel;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Base64;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FilterTaskAuthTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private FilterTaskAuth filter;

    private UserModel testUser;

    @BeforeEach
    void setUp() {
        testUser = new UserModel();
        testUser.setId(UUID.randomUUID());
        testUser.setUsername("john");
        testUser.setPassword(at.favre.lib.crypto.bcrypt.BCrypt.withDefaults().hashToString(12, "123456".toCharArray()));
    }

    @Test
    void shouldPassThroughForNonTaskPaths() throws Exception {
        when(request.getServletPath()).thenReturn("/users/create");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userRepository);
    }

    @Test
    void shouldAllowAccessWithValidCredentials() throws Exception {
        var credentials = Base64.getEncoder().encodeToString("john:123456".getBytes());
        when(request.getServletPath()).thenReturn("/tasks/");
        when(request.getHeader("Authorization")).thenReturn("Basic " + credentials);
        when(userRepository.findByUsername("john")).thenReturn(testUser);

        filter.doFilterInternal(request, response, filterChain);

        verify(request).setAttribute(eq("idUser"), any(UUID.class));
        verify(filterChain).doFilter(request, response);
        verify(response, never()).sendError(anyInt());
    }

    @Test
    void shouldReturn401WhenUserNotFound() throws Exception {
        var credentials = Base64.getEncoder().encodeToString("unknown:pass".getBytes());
        when(request.getServletPath()).thenReturn("/tasks/");
        when(request.getHeader("Authorization")).thenReturn("Basic " + credentials);
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(401);
        verify(filterChain, never()).doFilter(any(), any());
    }

    @Test
    void shouldReturn401WhenPasswordIsWrong() throws Exception {
        var credentials = Base64.getEncoder().encodeToString("john:wrongpass".getBytes());
        when(request.getServletPath()).thenReturn("/tasks/");
        when(request.getHeader("Authorization")).thenReturn("Basic " + credentials);
        when(userRepository.findByUsername("john")).thenReturn(testUser);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).sendError(401);
        verify(filterChain, never()).doFilter(any(), any());
    }
}
