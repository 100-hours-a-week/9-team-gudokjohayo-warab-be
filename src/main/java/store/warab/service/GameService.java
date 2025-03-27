package store.warab.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import store.warab.common.exception.NotFoundException;
import store.warab.dto.*;
import store.warab.entity.Category;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;
import store.warab.entity.User;
import store.warab.repository.CategoryRepository;
import store.warab.repository.GameDynamicRepository;
import store.warab.repository.GameStaticRepository;
import store.warab.repository.UserRepository;

@Service
public class GameService {
  private final GameStaticRepository gameStaticRepository;
  private final GameDynamicRepository gameDynamicRepository;
  private final CategoryRepository categoryRepository;
  private final UserRepository userRepository;

  public GameService(
      GameStaticRepository gameStaticRepository,
      GameDynamicRepository gameDynamicRepository,
      CategoryRepository categoryRepository,
      UserRepository userRepository) {
    this.gameStaticRepository = gameStaticRepository;
    this.gameDynamicRepository = gameDynamicRepository;
    this.categoryRepository = categoryRepository;
    this.userRepository = userRepository;
  }

  public GameDetailResponseDto getGameDetail(Long game_id) {
    // 1️⃣ GameStatic 조회 (게임이 존재하는지 확인)
    GameStatic game_static =
        gameStaticRepository
            .findById(game_id)
            .orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게임을 찾을 수 없습니다.") {});

    // 2️⃣ GameDynamic 조회 (존재하지 않을 수도 있음)
    GameDynamic game_dynamic = gameDynamicRepository.findById(game_id).orElse(null);

    // 3️⃣ DTO 변환 후 반환
    return new GameDetailResponseDto(game_static, game_dynamic);
  }

  public List<GameSearchResponseDto> filterGames(
      String query,
      Set<Long> categoryIds,
      Integer ratingMin,
      Integer ratingMax,
      Integer priceMin,
      Integer priceMax,
      Boolean singleplay,
      Boolean multiplay,
      Integer onlinePlayersMin,
      Integer onlinePlayersMax,
      String mode,
      String sort,
      Integer limit,
      Integer offset) {
    // ✅ 카테고리 검증: 존재하지 않는 ID가 포함된 경우 400 오류 반환
    if (categoryIds != null && !categoryIds.isEmpty()) {
      Set<Long> validCategoryIds = categoryRepository.findValidCategoryIds(categoryIds);
      if (validCategoryIds.size() != categoryIds.size()) {
        throw new IllegalArgumentException("Invalid category ID provided.");
      }
    }
    List<GameStatic> games =
        gameStaticRepository.findFilteredGames(
            query,
            categoryIds,
            ratingMin,
            ratingMax,
            priceMin,
            priceMax,
            singleplay,
            multiplay,
            onlinePlayersMin,
            onlinePlayersMax,
            mode,
            sort,
            limit,
            offset);

    return games.stream()
        .map(game -> new GameSearchResponseDto(game, game.getGame_dynamic()))
        .collect(Collectors.toList());
  }

  public List<MainPageResponseDto> getGamesForMainPage(Long userId) {
    User user =
        userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

    List<MainPageResponseDto> result = new ArrayList<>();

    // 1. 할인 게임
    List<GameStatic> discountedGames = gameStaticRepository.findTopDiscountedGames();
    List<GameInfoDto> discountedGamesList =
        discountedGames.stream()
            .map(
                discountedGame -> new GameInfoDto(discountedGame, discountedGame.getGame_dynamic()))
            .collect(Collectors.toList());
    result.add(new MainPageResponseDto("🔥 현재 할인 중인 게임이에요", discountedGamesList));

    // 2. 인기 게임
    List<GameStatic> popularGames = gameStaticRepository.findTopPopularGames();
    List<GameInfoDto> popularGamesList =
        popularGames.stream()
            .map(popularGame -> new GameInfoDto(popularGame, popularGame.getGame_dynamic()))
            .collect(Collectors.toList());
    result.add(new MainPageResponseDto("🏆 지금 인기 많은 게임이에요", popularGamesList));

    // 3. 카테고리별 추천 게임
    Set<Category> preferredCategories = user.getCategories();
    if (!preferredCategories.isEmpty()) {
      preferredCategories.stream()
          .limit(5)
          .forEach(
              category -> {
                List<GameStatic> games =
                    gameStaticRepository.findTop10ByCategoryId(category.getId());
                List<GameInfoDto> gameList =
                    games.stream()
                        .map(game -> new GameInfoDto(game, game.getGame_dynamic()))
                        .collect(Collectors.toList());
                result.add(
                    new MainPageResponseDto(
                        "🎮 " + category.getCategoryName() + " 게임이에요", gameList));
              });
    }
    return result;
  }

  public GameLowestPriceDto getLowestPrice(Long id) {
    GameDynamic gameDynamic =
        gameDynamicRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException("게임이 존재하지 않습니다."));
    return new GameLowestPriceDto(gameDynamic);
  }
}
