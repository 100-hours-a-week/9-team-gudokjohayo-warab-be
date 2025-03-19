package store.warab.service;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import store.warab.dto.GameSearchResponseDTO;
import store.warab.entity.GameDynamic;
import store.warab.entity.GameStatic;
import store.warab.repository.CategoryRepository;
import store.warab.repository.GameDynamicRepository;
import store.warab.repository.GameStaticRepository;

@Service
public class GameSearchService {
    private final GameStaticRepository gameStaticRepository;
    private final GameDynamicRepository gameDynamicRepository;
    private final CategoryRepository categoryRepository;

    public GameSearchService(
        GameStaticRepository gameStaticRepository, GameDynamicRepository gameDynamicRepository, CategoryRepository categoryRepository) {
        this.gameStaticRepository = gameStaticRepository;
        this.gameDynamicRepository = gameDynamicRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<GameSearchResponseDTO> filterGames(
        String query,
        List<Long> categoryIds,
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
            List<Long> validCategoryIds = categoryRepository.findValidCategoryIds(categoryIds);
            if (validCategoryIds.size() != categoryIds.size()) {
                throw new IllegalArgumentException("Invalid category ID provided.");
            }
        }
        limit = (limit == null) ? Integer.MAX_VALUE : limit;

        List<GameStatic> games = gameStaticRepository.findFilteredGames(categoryIds, query, priceMin, priceMax, playersMin, playersMax, onlinePlayersMin, onlinePlayersMax, sort, limit);

        return games.stream()
            // ✅ 1️⃣ 카테고리 필터 먼저 적용
            .filter(game -> {
                if (categoryIds == null || categoryIds.isEmpty()) return true; // 필터 없으면 통과

                List<Long> gameCategoryIds = game.getGame_categories().stream()
                    .map(gc -> gc.getCategory().getId()) // ✅ Category ID 가져오기
                    .collect(Collectors.toList());

                return gameCategoryIds.stream().anyMatch(categoryIds::contains); // 하나라도 포함되면 통과
            })
            // ✅ 2️⃣ query 필터 (제목 검색)
            .filter(game -> query == null || game.getTitle().toLowerCase().contains(query.toLowerCase()))
            // ✅ 3️⃣ price 필터 (최소, 최대 가격)
            .filter(game -> (priceMin == null || game.getPrice() >= priceMin) &&
                (priceMax == null || game.getPrice() <= priceMax))
            // ✅ 4️⃣ players 필터 (최소, 최대 플레이어)
            .filter(game -> (playersMin == null || game.getPlayer_count() >= playersMin) &&
                (playersMax == null || game.getPlayer_count() <= playersMax))
            // ✅ 5️⃣ game_dynamic에서 rating 필터 적용
            .map(game -> {
                GameDynamic gameDynamic = gameDynamicRepository.findById(game.getId()).orElse(null);
                if (gameDynamic == null ||
                    (ratingMin == null || gameDynamic.getRating() >= ratingMin) &&
                        (ratingMax == null || gameDynamic.getRating() <= ratingMax)) {
                    return new GameSearchResponseDTO(game, gameDynamic);
                }
                return null;
            })
            // ✅ null 값 제거
            .filter(dto -> dto != null)
            // ✅ 6️⃣ 최대 개수 제한
            .limit(limit != null ? limit : 20)
            .collect(Collectors.toList());
    }
}
