package store.warab.controller;

import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.warab.common.exception.BadRequestException;
import store.warab.common.util.ApiResponse;
import store.warab.dto.*;
import store.warab.service.AuthService;
import store.warab.service.GameService;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {
  private final GameService gameService;
  private final AuthService authService;

  public GameController(GameService gameService, AuthService authService) {
    System.out.println("create GameController");
    this.gameService = gameService;
    this.authService = authService;
  }

  /// api/v1/games/{id}
  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse> getGameDetail(@PathVariable Long id) {
    GameDetailResponseDto data = gameService.getGameDetail(id);
    return ResponseEntity.ok(new ApiResponse("game_detail_info_inquiry_success", data));
  }

  /// api/v1/games?
  @GetMapping
  public ResponseEntity<ApiResponse> getFilteredGames(
      @RequestParam(required = false) String query,
      @RequestParam(required = false) List<Long> category_ids,
      @RequestParam(required = false) Integer rating_min,
      @RequestParam(required = false) Integer rating_max,
      @RequestParam(required = false) Integer price_min,
      @RequestParam(required = false) Integer price_max,
      @RequestParam(value = "single_play", required = false) Boolean singleplay,
      @RequestParam(value = "multi_play", required = false) Boolean multiplay,
      @RequestParam(required = false) Integer online_players_min,
      @RequestParam(required = false) Integer online_players_max,
      @RequestParam(required = false) String mode,
      @RequestParam(required = false) String sort,
      //  limit과 page에 default 값을 설정
      @RequestParam(value = "limit", defaultValue = "10") Integer limit,
      @RequestParam(value = "page", defaultValue = "0") Integer page,
      @CookieValue(value = "jwt", required = false) String token) {
    if (category_ids != null && category_ids.size() > 5) {
      throw new BadRequestException("카테고리는 5개까지 선택가능합니다.");
    }
    // page를 offset으로 변환
    Integer offset = page * limit;

    List<GameSearchResponseDto> games =
        gameService.filterGames(
            token,
            query,
            category_ids,
            rating_min,
            rating_max,
            price_min,
            price_max,
            singleplay,
            multiplay,
            online_players_min,
            online_players_max,
            mode,
            sort,
            limit,
            offset);
    Map<String, Object> data = new HashMap<>();
    data.put("games", games);
    return ResponseEntity.ok(new ApiResponse("game_list_inquiry_success", data));
  }

  //  api/v1/games/main
  @GetMapping("/main")
  public ResponseEntity<ApiResponse> getMainPage(
      @CookieValue(value = "jwt", required = false) String token) {
    List<MainPageResponseDto> games = gameService.getGamesForMainPage(token);
    Map<String, Object> data = new HashMap<>();
    data.put("games", games);
    return ResponseEntity.ok(new ApiResponse("main_page_inquiry_success", data));
  }

  @GetMapping("/{id}/lowest-price")
  public ResponseEntity<ApiResponse> getLowestPrice(@PathVariable Long id) {
    GameLowestPriceDto data = gameService.getLowestPrice(id);
    return ResponseEntity.ok(new ApiResponse("get_lowest_price_success", data));
  }

  // api/v1/games/prices_by_platform/{gameId}
  @GetMapping("prices_by_platform/{gameId}")
  public ResponseEntity<ApiResponse> getPricesByPlatform(@PathVariable Long gameId) {
    if (gameId <= 0) {
      throw new BadRequestException("게임 ID는 0보다 커야 합니다.");
    }

    Integer currentPrice = gameService.getCurrentPrice(gameId);
    List<PlatformDiscountInfoDto> discountInfo =
        gameService.getDiscountInfoByGameId(gameId); // 플랫폼별 할인

    GameDiscountInfoDto response = new GameDiscountInfoDto(currentPrice, discountInfo);

    return ResponseEntity.ok(new ApiResponse("get_prices_by_platform_success", response));
  }

  //  api/v1/games/lowest_price_link/{game_id}
  @GetMapping("lowest_price_link/{gameId}")
  public ResponseEntity<ApiResponse> getLowestPriceLink(@PathVariable Long gameId) {
    if (gameId <= 0) {
      throw new BadRequestException("게임 ID는 0보다 커야 합니다.");
    }
    LowestPriceLinkDto response = gameService.getLowestPriceLink(gameId);

    return ResponseEntity.ok(new ApiResponse("get_lowest_price_link_success", response));
  }
}
