package store.warab.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import store.warab.dto.GameDetailResponseDto;
import store.warab.dto.GameInfoDto;
import store.warab.dto.GameSearchResponseDto;
import store.warab.dto.MainPageResponseDto;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;
import store.warab.repository.CategoryRepository;
import store.warab.repository.GameDynamicRepository;
import store.warab.repository.GameStaticRepository;

@Service
public class GameService {
  private final GameStaticRepository gameStaticRepository;
  private final GameDynamicRepository gameDynamicRepository;
  private final CategoryRepository categoryRepository;

  public GameService(
      GameStaticRepository gameStaticRepository,
      GameDynamicRepository gameDynamicRepository,
      CategoryRepository categoryRepository) {
    this.gameStaticRepository = gameStaticRepository;
    this.gameDynamicRepository = gameDynamicRepository;
    this.categoryRepository = categoryRepository;
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
      Integer playersMin,
      Integer playersMax,
      Integer onlinePlayersMin,
      Integer onlinePlayersMax,
      String mode,
      String sort,
      Integer limit) {
    // ✅ 카테고리 검증: 존재하지 않는 ID가 포함된 경우 400 오류 반환
    if (categoryIds != null && !categoryIds.isEmpty()) {
      Set<Long> validCategoryIds = categoryRepository.findValidCategoryIds(categoryIds);
      if (validCategoryIds.size() != categoryIds.size()) {
        throw new IllegalArgumentException("Invalid category ID provided.");
      }
    }
    limit = (limit == null) ? 10 : limit;
    List<GameStatic> games =
        gameStaticRepository.findFilteredGames(
            categoryIds,
            query,
            priceMin,
            priceMax,
            playersMin,
            playersMax,
            onlinePlayersMin,
            onlinePlayersMax,
            sort,
            mode,
            limit);

    return games.stream()
        .map(game -> new GameSearchResponseDto(game, game.getGame_dynamic()))
        .collect(Collectors.toList());
  }

    public List<MainPageResponseDto> getGamesForMainPage()
    {
        List<GameStatic> discountedGames = gameStaticRepository.findTopDiscountedGames();
        List<GameInfoDto> discountedGamesList = discountedGames.stream()
            .map(discountedGame -> new GameInfoDto(discountedGame, discountedGame.getGame_dynamic()))
            .collect(Collectors.toList());

        List<GameStatic> popularGames = gameStaticRepository.findTopPopularGames();
        List<GameInfoDto> popularGamesList = popularGames.stream()
            .map(popularGame -> new GameInfoDto(popularGame, popularGame.getGame_dynamic()))
            .collect(Collectors.toList());

        // 3. 결과 리스트로 포장
        List<MainPageResponseDto> result = new ArrayList<>();
        result.add(new MainPageResponseDto("🔥 현재 할인 중인 게임이에요", discountedGamesList));
        result.add(new MainPageResponseDto("🏆 지금 인기 많은 게임이에요", popularGamesList));

        return result;
    }
}
