package store.warab.service;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.warab.common.exception.BadRequestException;
import store.warab.common.exception.NotFoundException;
import store.warab.dto.AuthUserResponseDto;
import store.warab.dto.UserProfileResponseDto;
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

  public UserProfileResponseDto getUserById(long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다" + userId));

    return UserProfileResponseDto.fromEntity(user);
  }

  /**
   * 사용자 인증 정보 조회
   *
   * @param userId 사용자 ID
   * @return AuthUserResponseDto 사용자 인증 정보
   */
  public AuthUserResponseDto getAuthUserInfo(long userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다: " + userId));

    return AuthUserResponseDto.fromEntity(user);
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
        .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다: " + userId));
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
            .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다" + userId));

    // 닉네임의 양 옆 공백 제거
    request.setNickname(request.getNickname().trim());

    if (request.getNickname().isEmpty()) {
      throw new BadRequestException("닉네임은 공백으로 이루어질 수 없습니다.");
    }
    // 닉네임 중복 검사
    if (!user.getNickname().equals(request.getNickname())
        && isNicknameDuplicated(request.getNickname())) {
      throw new BadRequestException("이미 사용 중인 닉네임입니다.");
    }

    // 디스코드 링크 유효성 및 중복 검사
    if (request.getDiscordLink() != null && !request.getDiscordLink().isEmpty()) {
      // 본인이 사용 중인 링크가 아닌 경우에만 중복 검사
      boolean isDuplicate;
      if (!request.getDiscordLink().equals(user.getDiscordLink())) {
        if (isDiscordLinkDuplicated(request.getDiscordLink()))
          throw new BadRequestException("이미 등록된 디스코드 링크 입니다.");
      }

      // 유효성 및 중복 검사
      discordService.validateDiscordLink(request.getDiscordLink());
    }

    // 닉네임, 디스코드 업데이트
    user.setNickname(request.getNickname());
    user.setDiscordLink(request.getDiscordLink());

    // 카테고리 ID 검증 및 설정
    if (request.getCategoryIds().isEmpty()) {
      user.setCategories(null);
    } else { // Bean Validation 사용하여 null check 생략 가능.
      Set<Long> validCategoryIds =
          categoryRepository.findValidCategoryIds(request.getCategoryIds());
      if (validCategoryIds.size() > 5) {
        throw new BadRequestException("카테고리는 5개까지 설정 가능합니다.");
      }
      Set<Category> preferredCategories =
          new HashSet<>(categoryRepository.findAllById(validCategoryIds));
      user.setCategories(preferredCategories);
    }
  }
}
