package store.warab.controller;

import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.warab.common.exception.BadRequestException;
import store.warab.common.util.ApiResponse;
import store.warab.dto.GameDetailResponseDto;
import store.warab.dto.GameLowestPriceDto;
import store.warab.dto.GameSearchResponseDto;
import store.warab.dto.MainPageResponseDto;
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
      @CookieValue("jwt") String token) {
    Long tokenUserId = authService.extractUserId(token);
    if (category_ids.size() > 5) {
      throw new BadRequestException("카테고리는 5개까지 선택가능합니다.");
    }
    // page를 offset으로 변환
    Integer offset = page * limit;

    List<GameSearchResponseDto> games =
        gameService.filterGames(
            tokenUserId,
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
  public ResponseEntity<ApiResponse> getMainPage(@CookieValue("jwt") String token) {
    Long tokenUserId = authService.extractUserId(token);
    List<MainPageResponseDto> games = gameService.getGamesForMainPage(tokenUserId);
    Map<String, Object> data = new HashMap<>();
    data.put("games", games);
    return ResponseEntity.ok(new ApiResponse("main_page_inquiry_success", data));
  }

  @GetMapping("/{id}/lowest-price")
  public ResponseEntity<ApiResponse> getLowestPrice(@PathVariable Long id) {
    GameLowestPriceDto data = gameService.getLowestPrice(id);
    return ResponseEntity.ok(new ApiResponse("get_lowest_price_success", data));
  }
}
