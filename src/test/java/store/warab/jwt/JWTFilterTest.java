// package store.warab.jwt;
//
// import jakarta.servlet.FilterChain;
// import jakarta.servlet.ServletException;
// import jakarta.servlet.http.Cookie;
// import jakarta.servlet.http.HttpServletRequest;
// import jakarta.servlet.http.HttpServletResponse;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.test.web.servlet.MockMvc;
// import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
// import java.io.IOException;
//
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.junit.jupiter.api.Assertions.assertNull;
// import static org.mockito.Mockito.*;
//
// public class JWTFilterTest {
//
//    @Mock
//    private JWTUtil jwtUtil;
//
//    @InjectMocks
//    private JWTFilter jwtFilter;
//
//    @Mock
//    private FilterChain filterChain;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        jwtFilter = new JWTFilter(jwtUtil);
//        mockMvc = MockMvcBuilders.standaloneSetup().addFilter(jwtFilter).build();
//    }
//
//    @Test
//    public void testDoFilterInternal_withValidToken() throws ServletException, IOException {
//        // Given
//        String token = "validToken";
//        String username = "testUser";
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        HttpServletResponse response = mock(HttpServletResponse.class);
//
//        when(jwtUtil.isExpired(token)).thenReturn(false);
//        when(jwtUtil.getUsername(token)).thenReturn(username);
//
//        Cookie jwtCookie = new Cookie("jwt", token);
//        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
//
//        // When
//        jwtFilter.doFilterInternal(request, response, filterChain);
//
//        // Then
//        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
//        verify(filterChain, times(1)).doFilter(request, response);
//        SecurityContextHolder.clearContext(); // 테스트가 끝난 후 반드시 context 초기화
//    }
//
//    @Test
//    public void testDoFilterInternal_withExpiredToken() throws ServletException, IOException {
//        // Given
//        String token = "expiredToken";
//        HttpServletRequest request = mock(HttpServletRequest.class);
//        HttpServletResponse response = mock(HttpServletResponse.class);
//
//        when(jwtUtil.isExpired(token)).thenReturn(true);
//
//        Cookie jwtCookie = new Cookie("jwt", token);
//        when(request.getCookies()).thenReturn(new Cookie[]{jwtCookie});
//
//        // When
//        jwtFilter.doFilterInternal(request, response, filterChain);
//
//        // Then
//        assertNull(SecurityContextHolder.getContext().getAuthentication());
//        verify(filterChain, times(1)).doFilter(request, response);
//        SecurityContextHolder.clearContext(); // 테스트가 끝난 후 반드시 context 초기화
//    }
//
// }
