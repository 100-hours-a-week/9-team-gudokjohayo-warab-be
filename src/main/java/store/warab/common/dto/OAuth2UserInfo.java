package store.warab.common.dto;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import store.warab.entity.User;

@Builder
@Getter
@ToString
public class OAuth2UserInfo {
  private String kakao_id;
  private String nickname;

  public static OAuth2UserInfo ofKakao(Map<String, Object> attributes) {
    return OAuth2UserInfo.builder()
        .kakao_id(attributes.get("id").toString()) // 카카오 고유 ID를 String으로 변환
        .nickname((String) ((Map) attributes.get("properties")).get("profile_nickname"))
        .build();
  }

  public User toEntity() {
    return User.builder().kakaoId(kakao_id).nickname(nickname).build();
  }
}
