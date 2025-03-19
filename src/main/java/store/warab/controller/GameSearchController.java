package store.warab.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.warab.dto.GameSearchResponseDTO;
import store.warab.service.GameSearchService;

@RestController
@RequestMapping("/v1/games")
///v1/games?query=elden&&category_ids=1&category_ids=2&category_ids=3
//    &rating_min=4&rating_max=5
//    &price_min=0&price_max=100000
//    &players_min=2&players_max=8
//    &online_players_min=0&online_players_max=1000&
//mode=default&sort=price_asc&limit=10

public class GameSearchController {
  private final GameSearchService gameSearchService;

  public GameSearchController(GameSearchService gameSearchService) {
    System.out.println("create GameController");
    this.gameSearchService = gameSearchService;
  }

  @GetMapping
  public List<GameSearchResponseDTO> getFilteredGames(
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
    return gameSearchService.filterGames(
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
