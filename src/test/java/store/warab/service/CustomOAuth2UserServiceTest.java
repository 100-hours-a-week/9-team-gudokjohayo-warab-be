// package store.warab.service;
//
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.mockito.InjectMocks;
// import org.mockito.Mock;
// import org.mockito.MockitoAnnotations;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.security.oauth2.client.registration.ClientRegistration;
// import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
// import org.springframework.security.oauth2.core.user.OAuth2User;
// import store.warab.entity.User;
// import store.warab.repository.UserRepository;
// import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
//
// import java.util.HashMap;
// import java.util.Map;
// import java.util.Optional;
//
// import static org.junit.jupiter.api.Assertions.assertNotNull;
// import static org.mockito.Mockito.*;
//
// public class CustomOAuth2UserServiceTest {
//
//    @Mock
//    private UserRepository userRepository;
//
//    @Mock
//    private ClientRegistrationRepository clientRegistrationRepository;
//
//    @InjectMocks
//    private CustomOAuth2UserService customOAuth2UserService;
//
//    @BeforeEach
//    public void setUp() {
//        //MockitoAnnotations.openMocks(this);
//        //customOAuth2UserService = new CustomOAuth2UserService(userRepository,
// clientRegistrationRepository);
//        MockitoAnnotations.openMocks(this);
//        customOAuth2UserService = spy(new CustomOAuth2UserService(userRepository,
// clientRegistrationRepository));
//    }
//
//    @Test
//    public void testLoadUser_withNewUser() {
//        // Given
//        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
//        OAuth2User oAuth2User = mock(OAuth2User.class);
//        ClientRegistration clientRegistration = mock(ClientRegistration.class);
//
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("id", "12345");
//        attributes.put("properties", Map.of("profile_nickname", "testNickname"));
//
//        when(oAuth2User.getAttributes()).thenReturn(attributes);
//        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
//        when(clientRegistration.getRegistrationId()).thenReturn("kakao");
//
//        when(userRepository.findByKakaoId("12345")).thenReturn(Optional.empty());
//
//        when(customOAuth2UserService.loadUser(userRequest)).thenReturn(oAuth2User);
//
//        // When
//        OAuth2User result = customOAuth2UserService.loadUser(userRequest);
//
//        // Then
//        assertNotNull(result);
//        verify(userRepository, times(1)).save(any(User.class));  // save() 메서드 호출 검증
//    }
//
//    @Test
//    public void testLoadUser_withExistingUser() {
//        // Given
//        OAuth2UserRequest userRequest = mock(OAuth2UserRequest.class);
//        OAuth2User oAuth2User = mock(OAuth2User.class);
//        ClientRegistration clientRegistration = mock(ClientRegistration.class);
//
//        // Mocking OAuth2User
//        Map<String, Object> attributes = new HashMap<>();
//        attributes.put("id", "12345");
//        attributes.put("properties", Map.of("profile_nickname", "testNickname"));
//
//        when(oAuth2User.getAttributes()).thenReturn(attributes);
//        when(userRequest.getClientRegistration()).thenReturn(clientRegistration);
//        when(clientRegistration.getRegistrationId()).thenReturn("kakao");
//
//        // Mocking the repository call
//        User existingUser = new User();
//        when(userRepository.findByKakaoId("12345")).thenReturn(Optional.of(existingUser));
//
//        // Mocking super.loadUser() call
//        doReturn(oAuth2User).when(customOAuth2UserService).loadUser(userRequest);
//
//        // When
//        OAuth2User result = customOAuth2UserService.loadUser(userRequest);
//
//        // Then
//        assertNotNull(result);
//        verify(userRepository, never()).save(any(User.class));
//    }
//
// }
