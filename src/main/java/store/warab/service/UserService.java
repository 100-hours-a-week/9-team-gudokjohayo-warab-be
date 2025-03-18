package store.warab.service;

import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import store.warab.dto.UserDto;
import store.warab.entity.UserEntity;
import store.warab.repository.UserRepository;

@Service
public class UserService {

  private static UserRepository userRepository;

  public UserDto getUserById(long userId) {
      UserEntity user = userRepository.findById(userId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다" + userId) {
      });
      return UserDto.fromEntity(user);
  }

  public boolean isNicknameDuplicated(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  public boolean isDiscordLinkDuplicated(String discordLink) {
    return userRepository.existsByDiscordLink(discordLink);
  }

  public String getUserNicknameById(long userId) {
    Optional<UserEntity> user = userRepository.findById(userId);

    return user.map(UserEntity::getNickname).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다: " + userId));
    return user.map(UserEntity::getNickname)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다: " + userId));
  }

  public String getUserDiscordLinkById(long userId) {
    Optional<UserEntity> user = userRepository.findById(userId);

    return user.map(UserEntity::getDiscordLink).orElse(null);
  }

  public UserDto updateUserInfo(UserDto userDto) {
    UserEntity userEntity =
        userRepository
            .findById(userDto.getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다: " + userDto.getId()));
    UserEntity user =
        UserEntity.builder()
            .nickname(
                userDto.getNickname() != null ? userDto.getNickname() : userEntity.getNickname())
            .discordLink(
                userDto.getDiscordLink() != null
                    ? userDto.getDiscordLink()
                    : userEntity.getDiscordLink())
            .categories(
                userDto.getCategories() != null
                    ? userDto.getCategories()
                    : userEntity.getCategories())
            .build();
    UserEntity updatedUserEntity = userRepository.save(user);
    return UserDto.fromEntity(updatedUserEntity);
  }
}
