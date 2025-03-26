package store.warab.service;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import store.warab.dto.UserDto;
import store.warab.dto.UserUpdateResponseDto;
import store.warab.entity.Category;
import store.warab.entity.User;
import store.warab.repository.CategoryRepository;
import store.warab.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final CategoryRepository categoryRepository;
  private final DiscordService discordService;

  public UserDto getUserById(long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다" + userId) {});
    return UserDto.fromEntity(user);
  }

  public boolean isNicknameDuplicated(String nickname) {
    return userRepository.existsByNickname(nickname);
  }

  public boolean isDiscordLinkDuplicated(String discordLink) {
    return userRepository.existsByDiscordLink(discordLink);
  }

  public String getUserNicknameById(long userId) {
    Optional<User> user = userRepository.findById(userId);

    return user.map(User::getNickname)
        .orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다: " + userId));
  }

  public String getUserDiscordLinkById(long userId) {
    Optional<User> user = userRepository.findById(userId);

    return user.map(User::getDiscordLink).orElse(null);
  }

  @Transactional
  public void updateUserInfo(UserUpdateResponseDto userUpdateResponseDto) {
    User user =
        userRepository
            .findById(userUpdateResponseDto.getId())
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다: " + userUpdateResponseDto.getId()));

    // DiscordService 기능 호출
    if (userUpdateResponseDto.getDiscordLink() != null
        && !userUpdateResponseDto.getDiscordLink().isEmpty()) {
      boolean isDuplicate =
          !userUpdateResponseDto.getDiscordLink().equals(user.getDiscordLink())
              && isDiscordLinkDuplicated(userUpdateResponseDto.getDiscordLink());

      discordService.validateDiscordLink(userUpdateResponseDto.getDiscordLink(), isDuplicate);
    }

    // 닉네임 중복 체크 (현재 사용자의 닉네임이 아닌 경우에만)
    if (!userUpdateResponseDto.getNickname().equals(user.getNickname())
        && isNicknameDuplicated(userUpdateResponseDto.getNickname())) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미 사용 중인 닉네임입니다.");
    }

    // 닉네임, 디스코드 업데이트
    user.setNickname(userUpdateResponseDto.getNickname());
    user.setDiscordLink(userUpdateResponseDto.getDiscordLink());

    // 카테고리 ID 검증 및 설정
    Set<Long> validCategoryIds =
        categoryRepository.findValidCategoryIds(userUpdateResponseDto.getCategoriesSet());
    Set<Category> categorySet = new HashSet<>(categoryRepository.findAllById(validCategoryIds));
    user.setCategories(categorySet);
    //    User user =
    //        User.builder()
    //            .nickname(
    //                userUpdateResponseDto.getNickname() != null ?
    // userUpdateResponseDto.getNickname() : userEntity.getNickname())
    //            .discordLink(
    //                userUpdateResponseDto.getDiscordLink() != null
    //                    ? userUpdateResponseDto.getDiscordLink()
    //                    : userEntity.getDiscordLink())
    //            .categories(
    //                userUpdateResponseDto.getCategoriesSet() != null
    //                    ? userUpdateResponseDto.getCategoriesSet()
    //                    : userEntity.getCategories())
    //            .build();
    //    User updatedUserEntity = userRepository.save(user);
    //    return UserDto.fromEntity(updatedUserEntity);
  }
}
