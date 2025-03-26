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
import store.warab.dto.UserProfileUpdateRequest;
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
  public void updateUserInfo(UserProfileUpdateRequest request, Long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(
                () ->
                    new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "유저가 존재하지 않습니다: " + userId));

    // 닉네임, 디스코드 업데이트
    user.setNickname(request.getNickname());
    user.setDiscordLink(request.getDiscordLink());

    // 카테고리 ID 검증 및 설정
    if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
      Set<Long> validCategoryIds =
          categoryRepository.findValidCategoryIds(request.getCategoryIds());
      Set<Category> preferredCategories =
          new HashSet<>(categoryRepository.findAllById(validCategoryIds));
      user.setCategories(preferredCategories);
    }
  }
}
