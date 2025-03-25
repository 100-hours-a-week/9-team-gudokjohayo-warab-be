package store.warab.dto;

public interface OAuth2Response {
  // 제공자
  String getProvider();

  // 제공자에서 발급해주는 아이디
  String getProviderId();

  // 사용자 설정 이름
  String getName();
}
