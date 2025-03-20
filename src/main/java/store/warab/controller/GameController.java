package store.warab.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.warab.dto.GameDetailResponseDto;
import store.warab.dto.GameSearchResponseDto;
import store.warab.service.GameService;

@RestController
@RequestMapping("/v1/games")
public class GameController {
  private final GameService gameService;

  public GameController(GameService gameService) {
    System.out.println("create GameController");
    this.gameService = gameService;
  }

  @GetMapping("/{id}")
  public ResponseEntity<GameDetailResponseDto> getGameDetail(@PathVariable Long id) {
    GameDetailResponseDto gameDetail = gameService.getGameDetail(id);
    return ResponseEntity.ok(gameDetail);
  }

  @GetMapping
  public List<GameSearchResponseDto> getFilteredGames(
      @RequestParam(required = false) String query,
      @RequestParam(required = false) List<Long> category_ids,
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
    return gameService.filterGames(
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
  }
}
