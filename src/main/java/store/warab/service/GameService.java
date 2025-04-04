package store.warab.service;

import io.sentry.Sentry;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import store.warab.common.exception.NotFoundException;
import store.warab.dto.*;
import store.warab.entity.Category;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;
import store.warab.entity.User;
import store.warab.repository.GameDynamicRepository;
import store.warab.repository.GameStaticRepository;
import store.warab.repository.UserRepository;

@Service
public class GameService {
  private final GameStaticRepository gameStaticRepository;
  private final GameDynamicRepository gameDynamicRepository;
  private final AuthService authService;
  private final UserRepository userRepository;

  public GameService(
      GameStaticRepository gameStaticRepository,
      GameDynamicRepository gameDynamicRepository,
      AuthService authService,
      UserRepository userRepository) {
    this.gameStaticRepository = gameStaticRepository;
    this.gameDynamicRepository = gameDynamicRepository;
    this.authService = authService;
    this.userRepository = userRepository;
  }

  public GameDetailResponseDto getGameDetail(Long game_id) {
    Sentry.captureMessage("test용");
    // 1️⃣ GameStatic 조회 (게임이 존재하는지 확인)
    GameStatic game_static =
        gameStaticRepository
            .findById(game_id)
            .orElseThrow(() -> new NotFoundException("게임이 존재하지 않습니다."));

    // 2️⃣ GameDynamic 조회 (존재하지 않을 수도 있음)
    GameDynamic game_dynamic = gameDynamicRepository.findById(game_id).orElse(null);

    // 3️⃣ DTO 변환 후 반환
    return new GameDetailResponseDto(game_static, game_dynamic);
  }

  public List<GameSearchResponseDto> filterGames(
      String token,
      String query,
      List<Long> categoryIds,
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

    List<GameStatic> games;
    if ("recommended".equals(mode)) {
      Long userId = authService.extractUserId(token);

      User user =
          userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
      Set<Category> preferredCategories = user.getCategories();

      // 일단 아래에 기능이 보장된 카테고리 필터링과 과정을 똑같게 하기 위해 자료형을 두번에 걸쳐 바꿈.
      categoryIds = preferredCategories.stream().map(Category::getId).collect(Collectors.toList());
      Long[] categoryIdsArray = categoryIds.toArray(new Long[0]);
      games =
          gameStaticRepository.findFilteredGamesWithCategory(
              query,
              categoryIdsArray,
              ratingMin,
              ratingMax,
              priceMin,
              priceMax,
              singleplay,
              multiplay,
              onlinePlayersMin,
              onlinePlayersMax,
              mode,
              limit,
              offset);
    } else if (categoryIds == null || categoryIds.isEmpty()) {
      games =
          gameStaticRepository.findFilteredGamesWithoutCategory(
              query,
              ratingMin,
              ratingMax,
              priceMin,
              priceMax,
              singleplay,
              multiplay,
              onlinePlayersMin,
              onlinePlayersMax,
              mode,
              limit,
              offset);
    } else { // 카테고리 필터가 걸려서 온 경우
      Long[] categoryIdsArray = categoryIds.toArray(new Long[0]);

      games =
          gameStaticRepository.findFilteredGamesWithCategory(
              query,
              categoryIdsArray,
              ratingMin,
              ratingMax,
              priceMin,
              priceMax,
              singleplay,
              multiplay,
              onlinePlayersMin,
              onlinePlayersMax,
              mode,
              limit,
              offset);
    }

    return games.stream()
        .map(game -> new GameSearchResponseDto(game, game.getGame_dynamic()))
        .collect(Collectors.toList());
  }

  public List<MainPageResponseDto> getGamesForMainPage(String token) {
    Long userId = null;
    if (authService.isValid(token)) {
      userId = authService.extractUserId(token); // 유효하지 않으면 null
    }

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
    if (userId != null) {
      User user =
          userRepository
              .findById(userId)
              .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
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
    }
    return result;
  }

  public GameLowestPriceDto getLowestPrice(Long gameId) {
    GameStatic game_static =
        gameStaticRepository
            .findById(gameId)
            .orElseThrow(() -> new NotFoundException("게임이 존재하지 않습니다."));

    GameDynamic gameDynamic =
        gameDynamicRepository
            .findById(gameId)
            .orElseThrow(() -> new NotFoundException("해당 게임의 동적 정보가 존재하지 않습니다."));
    return new GameLowestPriceDto(gameDynamic);
  }
}
