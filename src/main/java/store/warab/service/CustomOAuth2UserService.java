package store.warab.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import store.warab.common.dto.OAuth2UserInfo;
import store.warab.common.security.CustomUserDetails;
import store.warab.entity.User;
import store.warab.repository.UserRepository;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. OAuth2 로그인 유저 정보를 가져옴
        OAuth2User oAuth2User = super.loadUser(userRequest);
        log.info("getAttributes : {}", oAuth2User.getAttributes());

        // 2. provider : kakao
        String provider = userRequest.getClientRegistration().getRegistrationId();
        log.info("provider: {}", provider);

        // 3. OAuth2UserInfo 객체 생성
        OAuth2UserInfo userInfo = OAuth2UserInfo.ofKakao(oAuth2User.getAttributes());
        String providerId = userInfo.getKakao_id();

        // 4. OAuth 인증을 위한 username
        String username = provider + " " + providerId;

        // 5. 유저 정보 조회 및 저장
        Optional<User> existingUser = userRepository.findByKakaoId(providerId);

        User user;
        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            String nickname = userInfo.getNickname();
            if (nickname == null || nickname.isEmpty()) {
                nickname = generateUniqueNickname();
            }

            user = User.builder()
                .kakaoId(providerId)
                .nickname(nickname)
                .build();
            userRepository.save(user);
        }

        return new CustomUserDetails(user, oAuth2User.getAttributes());
    }

    // 닉네임 자동 생성 메서드
    private String generateUniqueNickname() {
        String prefix = "user";
        Optional<String> latestNickname = userRepository.findLatestUserNickname(prefix + "%");

        if (latestNickname.isPresent()) {
            Pattern pattern = Pattern.compile(prefix + "(\\d+)");
            Matcher matcher = pattern.matcher(latestNickname.get());

            if (matcher.find()) {
                int number = Integer.parseInt(matcher.group(1));
                return prefix + (number + 1);
            }
        }
        return prefix + "1";
    }
}
