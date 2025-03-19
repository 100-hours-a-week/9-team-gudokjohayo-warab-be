package store.warab.repository;

import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import store.warab.entity.GameStatic;

@Repository
public interface GameStaticRepository extends JpaRepository<GameStatic, Long> {

    // ✅ 기존 findAll() 유지 (필요한 경우를 대비)
//    @EntityGraph(attributePaths = {"gameDynamic"}) // ✅ N+1 문제 해결: gameDynamic을 한 번에 가져오기
    List<GameStatic> findAll();

    // ✅ DB에서 직접 필터링하여 가져오는 메서드 추가
    // ✅ DB에서 직접 필터링하여 가져오는 메서드 (COALESCE 포함)
    @Query("SELECT gs FROM GameStatic gs " +
        "LEFT JOIN gs.game_dynamic gd " +  // ✅ game_dynamic과 조인 추가 (game_dynamic 필드 존재 확인 필요)
        "JOIN gs.game_categories gc " + // ✅ gameCategories가 GameStatic에 존재하는지 확인 필요
        "WHERE (:category_ids IS NULL OR gc.category.id IN :category_ids) " +
//        "AND (:query IS NULL OR LOWER(gs.title) LIKE LOWER(CONCAT('%', :query, '%'))) " +
        "AND (COALESCE(:query, '') = '' OR LOWER(gs.title) LIKE LOWER(CONCAT('%', :query, '%'))) " +

        "AND (:price_min IS NULL OR gs.price >= :price_min) " +
        "AND (:price_max IS NULL OR gs.price <= :price_max) " +
        "AND (:players_min IS NULL OR gs.player_count >= :players_min) " +
        "AND (:players_max IS NULL OR gs.player_count <= :players_max) " +
        "AND (:online_players_min IS NULL OR gd.active_players >= :online_players_min) " +
        "AND (:online_players_max IS NULL OR gd.active_players <= :online_players_max) " +
        "ORDER BY " +
        "COALESCE(CASE WHEN :sort = 'price_asc' THEN gs.price END, gs.price) ASC, " +
        "COALESCE(CASE WHEN :sort = 'price_desc' THEN gs.price END, gs.price) DESC, " +
        "COALESCE(CASE WHEN :sort = 'rating_desc' THEN gd.rating END, gd.rating) DESC " +
        // postgresql이라서 limit null이어도 무시가 되지만 다른 db의 경우 에러 발생 가능성 있음. 추후 확장성 위해 수정 필요.
        "LIMIT :limit")
    List<GameStatic> findFilteredGames(
        @Param("category_ids") List<Long> category_ids,
        @Param("query") String query,
        @Param("price_min") Integer price_min,
        @Param("price_max") Integer price_max,
        @Param("players_min") Integer players_min,
        @Param("players_max") Integer players_max,
        @Param("online_players_min") Integer online_players_min,
        @Param("online_players_max") Integer online_players_max,
        @Param("sort") String sort,
        @Param("limit") Integer limit);
}
