package store.warab.repository;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Set;
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

  // ✅ DB에서 직접 필터링하여 가져오는 메서드 추가
  //  @Query(
  //      "SELECT  gs FROM GameStatic gs "
  //          + "LEFT JOIN gs.game_dynamic gd "
  //          //          ✅ game_dynamic과 조인 추가 (game_dynamic 필드 존재 확인 필요)
  //          //          "JOIN gs.game_categories gc "
  //          + "LEFT JOIN gs.game_categories gc " // ✅ INNER JOIN → LEFT JOIN 변경
  //          + // ✅ gameCategories가 GameStatic에 존재하는지 확인 필요
  ////          "WHERE (:category_ids IS NULL OR gc.category.id IN :category_ids) "
  ////          +
  //          "where (COALESCE(:query, '') = '' OR LOWER(gs.title) LIKE LOWER(CONCAT('%', :query,
  // '%'))) "
  //          + "and (:price_min IS NULL OR gs.price >= :price_min) "
  ////          + "AND (:price_max IS NULL OR gs.price <= :price_max) "
  ////          + "AND (:online_players_min IS NULL OR gd.active_players >= :online_players_min) "
  ////          + "AND (:online_players_max IS NULL OR gd.active_players <= :online_players_max) "
  ////          + "AND (:mode != 'discounted' OR gd.on_sale = true) "  // ✅ mode가 discounted일 경우
  // on_sale 필터 적용
  //          + "ORDER BY gd.total_reviews DESC "
  //          + "LIMIT :limit")
  ////  )
  @Query(
      value =
          "SELECT DISTINCT ON (gs.id) gs.* "
              + "FROM game_static gs "
              + "LEFT JOIN game_dynamic gd ON gs.id = gd.game_id "
              + "LEFT JOIN game_category gc ON gs.id = gc.game_id "
              + "WHERE (COALESCE(:query, '') = '' OR LOWER(gs.title) LIKE LOWER(CONCAT('%', :query, '%'))) "
              + "AND (:price_min IS NULL OR gs.price >= :price_min) "
              + "AND (:mode IS NULL OR "
              + ":mode = 'default' OR"
              + "(:mode = 'discounted' AND gd.on_sale = true)) "
              + "ORDER BY gs.id, gd.total_reviews DESC "
              + "LIMIT :limit",
      nativeQuery = true)
  List<GameStatic> findFilteredGames(
      @Param("category_ids") Set<Long> category_ids,
      @Param("query") String query,
      @Param("price_min") Integer price_min,
      @Param("price_max") Integer price_max,
      @Param("players_min") Integer players_min,
      @Param("players_max") Integer players_max,
      @Param("online_players_min") Integer online_players_min,
      @Param("online_players_max") Integer online_players_max,
      @Param("sort") String sort,
      @Param("mode") String mode,
      @Param("limit") Integer limit);

  @Query(
      value =
          "SELECT DISTINCT ON (gs.id) gs.* "
              + "FROM game_static gs "
              + "LEFT JOIN game_dynamic gd ON gs.id = gd.game_id "
              + "LEFT JOIN game_category gc ON gs.id = gc.game_id "
              + "WHERE (gd.on_sale = true) "
              + "ORDER BY gs.id, gd.total_reviews DESC "
              + "LIMIT 10",
      nativeQuery = true)
  List<GameStatic> findTopDiscountedGames();

  @Query(
      value =
          "SELECT DISTINCT ON (gs.id) gs.* "
              + "FROM game_static gs "
              + "LEFT JOIN game_dynamic gd ON gs.id = gd.game_id "
              + "LEFT JOIN game_category gc ON gs.id = gc.game_id "
              + "ORDER BY gs.id, gd.total_reviews DESC "
              + "LIMIT 10",
      nativeQuery = true)
  List<GameStatic> findTopPopularGames();

    @Query(
        value = """
        SELECT DISTINCT ON (gs.id) gs.*
        FROM game_static gs
        LEFT JOIN game_dynamic gd ON gs.id = gd.game_id
        LEFT JOIN game_category gc ON gs.id = gc.game_id
        WHERE gc.category_id = :categoryId
        ORDER BY gs.id, gd.total_reviews DESC
        LIMIT 10
        """,
        nativeQuery = true)
    List<GameStatic> findTop10ByCategoryId(@Param("categoryId") Long categoryId);
}
