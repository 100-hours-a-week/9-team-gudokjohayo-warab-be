package store.warab.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import store.warab.dto.GameSearchResponseDTO;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;
import store.warab.repository.GameDynamicRepository;
import store.warab.repository.GameStaticRepository;

@Service
public class GameSearchService {
  private final GameStaticRepository gameStaticRepository;
  private final GameDynamicRepository gameDynamicRepository;

  public GameSearchService(
      GameStaticRepository gameStaticRepository, GameDynamicRepository gameDynamicRepository) {
    this.gameStaticRepository = gameStaticRepository;
    this.gameDynamicRepository = gameDynamicRepository;
  }

  public List<GameSearchResponseDTO> filterGames(
      String query,
      String categories,
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
    List<GameStatic> games = gameStaticRepository.findAll();

    return games.stream()
        // ✅ query가 null이 아닐 때만 필터 적용
        .filter(
            game -> query == null || game.getTitle().toLowerCase().contains(query.toLowerCase()))
        // ✅ price 필터: null이 아닌 경우만 필터링
        .filter(
            game ->
                (priceMin == null || game.getPrice() >= priceMin)
                    && (priceMax == null || game.getPrice() <= priceMax))
        // ✅ players 필터: null이 아닌 경우만 필터링
        .filter(
            game ->
                (playersMin == null || game.getPlayerCount() >= playersMin)
                    && (playersMax == null || game.getPlayerCount() <= playersMax))
        // ✅ game_dynamic에서 rating 필터링
        .map(
            game -> {
              GameDynamic gameDynamic = gameDynamicRepository.findById(game.getId()).orElse(null);
              if (gameDynamic == null
                  || (ratingMin == null || gameDynamic.getRating() >= ratingMin)
                      && (ratingMax == null || gameDynamic.getRating() <= ratingMax)) {
                return new GameSearchResponseDTO(game, gameDynamic);
              }
              return null;
            })
        // ✅ null 값 제거
        .filter(dto -> dto != null)
        .collect(Collectors.toList());
  }
}
