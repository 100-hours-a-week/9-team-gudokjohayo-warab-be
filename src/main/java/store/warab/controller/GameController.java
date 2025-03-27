package store.warab.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

  /// api/v1/games
  @GetMapping
  public ResponseEntity<ApiResponse> getFilteredGames(
      @RequestParam(required = false) String query,
      @RequestParam(required = false) Set<Long> category_ids,
      @RequestParam(required = false) Integer rating_min,
      @RequestParam(required = false) Integer rating_max,
      @RequestParam(required = false) Integer price_min,
      @RequestParam(required = false) Integer price_max,
      @RequestParam(required = false) Integer players_min,
      @RequestParam(required = false) Integer players_max,
      @RequestParam(required = false) Integer online_players_min,
      @RequestParam(required = false) Integer online_players_max,
      @RequestParam(required = false) String mode,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) Integer limit) {
    List<GameSearchResponseDto> games =
        gameService.filterGames(
            query,
            category_ids,
            rating_min,
            rating_max,
            price_min,
            price_max,
            players_min,
            players_max,
            online_players_min,
            online_players_max,
            mode,
            sort,
            limit);
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
