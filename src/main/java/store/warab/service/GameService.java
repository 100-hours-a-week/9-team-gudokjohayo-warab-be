package store.warab.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sentry.Sentry;
import java.util.*;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import store.warab.common.exception.NotFoundException;
import store.warab.dto.*;
import store.warab.entity.*;
import store.warab.repository.CurrentPriceByPlatformRepository;
import store.warab.repository.GameDynamicRepository;
import store.warab.repository.GameStaticRepository;
import store.warab.repository.GameVideoRepository;
import store.warab.repository.UserRepository;

@Service
@AllArgsConstructor
public class GameService {
  private final GameStaticRepository gameStaticRepository;
  private final GameDynamicRepository gameDynamicRepository;
  private final AuthService authService;
  private final UserRepository userRepository;
  private final GameVideoRepository gameVideoRepository;
  private final CurrentPriceByPlatformRepository currentPriceByPlatformRepository;
  private final Optional<CacheService> cacheService;
  private final ObjectMapper objectMapper;

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

  private void loadDiscountGamesFromDBAndCache(List<MainPageResponseDto> result) {
    List<GameStatic> discountedGames = gameStaticRepository.findTopDiscountedGames();
    List<GameInfoDto> discountedGamesList =
        discountedGames.stream()
            .map(
                discountedGame -> new GameInfoDto(discountedGame, discountedGame.getGame_dynamic()))
            .collect(Collectors.toList());
    try {
      String json = objectMapper.writeValueAsString(discountedGamesList);
      cacheService.get().cacheMainGames("discount", json); // 캐싱
    } catch (JsonProcessingException e) {
      System.err.println("캐싱용 JSON 직렬화 실패"); // 이건 optional 처리
    }
    result.add(new MainPageResponseDto("🔥 현재 할인 중인 게임이에요", discountedGamesList));
  }

  private void loadPopularGamesFromDBAndCache(List<MainPageResponseDto> result) {
    List<GameStatic> popularGames = gameStaticRepository.findTopPopularGames();
    List<GameInfoDto> popularGamesList =
        popularGames.stream()
            .map(popularGame -> new GameInfoDto(popularGame, popularGame.getGame_dynamic()))
            .collect(Collectors.toList());
    try {
      String json = objectMapper.writeValueAsString(popularGamesList);
      cacheService.get().cacheMainGames("popular", json); // 캐싱
    } catch (JsonProcessingException e) {
      System.err.println("캐싱용 JSON 직렬화 실패"); // 이건 optional 처리
    }
    result.add(new MainPageResponseDto("🏆 지금 인기 많은 게임이에요", popularGamesList));
  }

  public List<MainPageResponseDto> getGamesForMainPage(String token) {
    Long userId = null;
    if (authService.isValid(token)) {
      userId = authService.extractUserId(token); // 유효하지 않으면 null
    }

    List<MainPageResponseDto> result = new ArrayList<>();

    // 1. 할인 게임
    if (!cacheService.isPresent()) {
      List<GameStatic> discountedGames = gameStaticRepository.findTopDiscountedGames();
      List<GameInfoDto> discountedGamesList =
          discountedGames.stream()
              .map(
                  discountedGame ->
                      new GameInfoDto(discountedGame, discountedGame.getGame_dynamic()))
              .collect(Collectors.toList());
      result.add(new MainPageResponseDto("🔥 현재 할인 중인 게임이에요", discountedGamesList));
    } else if (cacheService.get().hasMainGamesCache("discount")) {
      try {
        String cached = cacheService.get().getCachedMainGames("discount");
        List<GameInfoDto> discountedGamesList =
            objectMapper.readValue(cached, new TypeReference<>() {});
        result.add(new MainPageResponseDto("🔥 현재 할인 중인 게임이에요", discountedGamesList));
      } catch (JsonProcessingException e) {
        // ❗ 캐시 파싱 실패 시 fallback
        System.err.println("캐시 파싱 실패 → DB에서 가져옵니다.");
        loadDiscountGamesFromDBAndCache(result);
      }
    } else {
      loadDiscountGamesFromDBAndCache(result);
    }

    // 2. 인기 게임
    if (!cacheService.isPresent()) {
      List<GameStatic> popularGames = gameStaticRepository.findTopPopularGames();
      List<GameInfoDto> popularGamesList =
          popularGames.stream()
              .map(popularGame -> new GameInfoDto(popularGame, popularGame.getGame_dynamic()))
              .collect(Collectors.toList());
      result.add(new MainPageResponseDto("🏆 지금 인기 많은 게임이에요", popularGamesList));
    } else if (cacheService.get().hasMainGamesCache("popular")) {
      try {
        String cached = cacheService.get().getCachedMainGames("popular");
        List<GameInfoDto> popularGamesList =
            objectMapper.readValue(cached, new TypeReference<>() {});
        result.add(new MainPageResponseDto("🏆 지금 인기 많은 게임이에요", popularGamesList));
      } catch (JsonProcessingException e) {
        // ❗ 캐시 파싱 실패 시 fallback
        System.err.println("캐시 파싱 실패 → DB에서 가져옵니다.");
        loadPopularGamesFromDBAndCache(result);
      }
    } else {
      loadPopularGamesFromDBAndCache(result);
    }

    // 3. 카테고리별 추천 게임
    if (userId != null) {
      User user =
          userRepository
              .findById(userId)
              .orElseThrow(() -> new NotFoundException("유저가 존재하지 않습니다."));
      Set<Category> preferredCategories = user.getCategories();
      if (!preferredCategories.isEmpty()) {
        if (cacheService.isPresent() && cacheService.get().hasMainGamesCache("preferred")) {}

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

  public List<GameVideoDto> getGameVideo(Long gameId) {
    GameStatic game =
        gameStaticRepository
            .findById(gameId)
            .orElseThrow(() -> new NotFoundException("게임을 찾을 수 없습니다."));

    List<GameVideo> gameVideoList = gameVideoRepository.findByGameStatic(game);
    return gameVideoList.stream().map(GameVideoDto::new).collect(Collectors.toList());
  }

  // 생각해보니 꼭 dto를 만들 필요가 없지 않나???
  //  public GameCurrentPriceDto getCurrentPrice(Long gameId) {
  //
  //  }
  // 그렇다면 이렇게 가능? ->
  public Integer getCurrentPrice(Long gameId) {
    GameStatic game_static =
        gameStaticRepository
            .findById(gameId)
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "게임이 존재하지 않습니다.")); // Optional 안에 값이 있으면 꺼내고,없으면 예외를 throw.

    return game_static.getPrice(); // 이렇게 바로 꺼내도 안전하려나?
  }

  public List<PlatformDiscountInfoDto> getDiscountInfoByGameId(Long gameId) {
    List<CurrentPriceByPlatform> lst =
        currentPriceByPlatformRepository.findAllByGameStatic_Id(gameId);

    if (lst.isEmpty()) {
      throw new NotFoundException("등록된 플랫폼별 할인정보가 존재하지 않습니다.");
    }

    return lst.stream().map(PlatformDiscountInfoDto::new).toList();
  }

  public LowestPriceLinkDto getLowestPriceLink(Long gameId) {
    List<PlatformDiscountInfoDto> discountInfo = getDiscountInfoByGameId(gameId);

    PlatformDiscountInfoDto cheapest =
        discountInfo.stream()
            .min(Comparator.comparing(PlatformDiscountInfoDto::getDiscountPrice))
            .orElseThrow(() -> new NotFoundException("할인 정보가 없습니다."));

    return new LowestPriceLinkDto(cheapest.getPlatform(), cheapest.getStoreUrl());
  }

  public List<String> autocomplete(String keyword) {
    if (keyword == null || keyword.isBlank()) {
      return gameStaticRepository.findGameTitlesByKeyword(""); // 기본값
    }

    // Redis 캐시가 있는 경우만 캐시 활용
    if (cacheService.isPresent() && cacheService.get().hasAutocompleteCache(keyword)) {
      return cacheService.get().getCachedAutocomplete(keyword);
    }

    List<String> result = gameStaticRepository.findGameTitlesByKeyword(keyword);

    // 캐시가 있다면 저장
    cacheService.ifPresent(service -> service.cacheAutocomplete(keyword, result));

    return result;
  }
}
