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
// v1/games?query=elden&categories=액션,전략
// &rating_min=4&rating_max=5
// &price_min=0&price_max=100000
// &players_min=2&players_max=8
// &online_players_min=0&online_players_max=1000&
// mode=default&sort=price_asc&limit=10
public class GameSearchController {
  private final GameSearchService gameSearchService;

  public GameSearchController(GameSearchService gameSearchService) {
    System.out.println("create GameController");
    this.gameSearchService = gameSearchService;
  }

  @GetMapping
  public List<GameSearchResponseDTO> getFilteredGames(
      @RequestParam(required = false) String query,
      @RequestParam(required = false) String categories,
      @RequestParam(required = false, defaultValue = "0") Integer ratingMin,
      @RequestParam(required = false, defaultValue = "10") Integer ratingMax,
      @RequestParam(required = false, defaultValue = "0") Integer priceMin,
      @RequestParam(required = false) Integer priceMax,
      @RequestParam(required = false) Integer playersMin,
      @RequestParam(required = false) Integer playersMax,
      @RequestParam(required = false) Integer onlinePlayersMin,
      @RequestParam(required = false) Integer onlinePlayersMax,
      @RequestParam(required = false) String mode,
      @RequestParam(required = false) String sort,
      @RequestParam(required = false) Integer limit) {
    return gameSearchService.filterGames(
        query,
        categories,
        ratingMin,
        ratingMax,
        priceMin,
        priceMax,
        playersMin,
        playersMax,
        onlinePlayersMin,
        onlinePlayersMax,
        mode,
        sort,
        limit);
  }
}
