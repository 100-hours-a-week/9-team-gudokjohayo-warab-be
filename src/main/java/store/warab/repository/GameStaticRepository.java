package store.warab.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import store.warab.entity.GameStatic;

@Repository
public interface GameStaticRepository extends JpaRepository<GameStatic, Long> {
  //    @EntityGraph(attributePaths = {"game_categories.category"})
  List<GameStatic> findAll();

  // ✅ 기존 findAll() 유지 (필요한 경우를 대비)
  //    @EntityGraph(attributePaths = {"gameDynamic"}) // ✅ N+1 문제 해결: gameDynamic을 한 번에 가져오기

  @Query(
      value =
          "SELECT gs.*, gd.* "
              + "FROM game_static gs "
              + "LEFT JOIN game_dynamic gd ON gs.id = gd.game_id "
              + "WHERE (COALESCE(:query, '') = '' OR LOWER(gs.title) LIKE LOWER(CONCAT('%', :query, '%'))) "
              + "AND (:price_min IS NULL OR gs.price >= :price_min) "
              + "AND (:price_max IS NULL OR gs.price <= :price_max) "
              + "AND (:rating_min IS NULL OR gd.rating >= :rating_min) "
              + "AND (:rating_max IS NULL OR gd.rating <= :rating_max) "
              + "AND (:singleplay IS NULL OR gs.is_singleplay = true) "
              + "AND (:multiplay IS NULL OR gs.is_multiplay = true) "
              + "AND (:online_players_min IS NULL OR gd.active_players >= :online_players_min) "
              + "AND (:online_players_max IS NULL OR gd.active_players <= :online_players_max) "
              + "AND (:mode IS NULL OR :mode = 'default' OR (:mode = 'discounted' AND gd.on_sale = true)) "
              + "ORDER BY gd.active_players DESC, gd.total_reviews DESC "
              + "LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  List<GameStatic> findFilteredGamesWithoutCategory(
      @Param("query") String query,
      @Param("rating_min") Integer rating_min,
      @Param("rating_max") Integer rating_max,
      @Param("price_min") Integer price_min,
      @Param("price_max") Integer price_max,
      @Param("singleplay") Boolean singleplay,
      @Param("multiplay") Boolean multiplay,
      @Param("online_players_min") Integer online_players_min,
      @Param("online_players_max") Integer online_players_max,
      @Param("mode") String mode,
      @Param("limit") Integer limit,
      @Param("offset") Integer offset);

  // ✅ categoryIds가 존재할 때 실행할 쿼리
  @Query(
      value =
          "SELECT gs.*, gd.* "
              + "FROM game_static gs "
              + "LEFT JOIN game_dynamic gd ON gs.id = gd.game_id "
              + "WHERE (COALESCE(:query, '') = '' OR LOWER(gs.title) LIKE LOWER(CONCAT('%', :query, '%'))) "
              + "AND (:price_min IS NULL OR gs.price >= :price_min) "
              + "AND (:price_max IS NULL OR gs.price <= :price_max) "
              + "AND (:rating_min IS NULL OR gd.rating >= :rating_min) "
              + "AND (:rating_max IS NULL OR gd.rating <= :rating_max) "
              + "AND (:singleplay IS NULL OR gs.is_singleplay = true) "
              + "AND (:multiplay IS NULL OR gs.is_multiplay = true) "
              + "AND (:online_players_min IS NULL OR gd.active_players >= :online_players_min) "
              + "AND (:online_players_max IS NULL OR gd.active_players <= :online_players_max) "
              + "AND (:mode IS NULL OR :mode = 'default' OR (:mode = 'discounted' AND gd.on_sale = true)) "
              + "AND (gs.id IN (SELECT game_id FROM game_category WHERE category_id = ANY(:category_ids))) "
              + "ORDER BY gd.active_players DESC, gd.total_reviews DESC "
              + "LIMIT :limit OFFSET :offset",
      nativeQuery = true)
  List<GameStatic> findFilteredGamesWithCategory(
      @Param("query") String query,
      @Param("category_ids") Long[] category_ids,
      @Param("rating_min") Integer rating_min,
      @Param("rating_max") Integer rating_max,
      @Param("price_min") Integer price_min,
      @Param("price_max") Integer price_max,
      @Param("singleplay") Boolean singleplay,
      @Param("multiplay") Boolean multiplay,
      @Param("online_players_min") Integer online_players_min,
      @Param("online_players_max") Integer online_players_max,
      @Param("mode") String mode,
      @Param("limit") Integer limit,
      @Param("offset") Integer offset);

  @Query(
      value =
          "SELECT gs.* "
              + "FROM game_static gs "
              + "LEFT JOIN game_dynamic gd ON gs.id = gd.game_id "
              + "WHERE (gd.on_sale = true) "
              + "ORDER BY gd.total_reviews DESC "
              + "LIMIT 10",
      nativeQuery = true)
  List<GameStatic> findTopDiscountedGames();

  @Query(
      value =
          "SELECT gs.* "
              + "FROM game_static gs "
              + "LEFT JOIN game_dynamic gd ON gs.id = gd.game_id "
              + "ORDER BY gd.total_reviews DESC "
              + "LIMIT 10",
      nativeQuery = true)
  List<GameStatic> findTopPopularGames();

  @Query(
      value = """
        SELECT gs.*
        FROM game_static gs
        LEFT JOIN game_dynamic gd ON gs.id = gd.game_id
        LEFT JOIN game_category gc ON gs.id = gc.game_id
        WHERE gc.category_id = :categoryId
        ORDER BY gd.total_reviews DESC
        LIMIT 10
        """,
      nativeQuery = true)
  List<GameStatic> findTop10ByCategoryId(@Param("categoryId") Long categoryId);
}
