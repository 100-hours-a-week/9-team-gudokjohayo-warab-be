package store.warab.service;

import java.time.Duration;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev", "prod"})
public class CacheService {

  private final StringRedisTemplate redisTemplate;
  private final String PREFIX_AUTOCOMPLETE = "autocomplete:";
  private final String PREFIX_MAIN = "main:";

  public CacheService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  // ✅ [1] 자동완성 캐시
  public void cacheAutocomplete(String keyword, List<String> suggestions) {
    String key = PREFIX_AUTOCOMPLETE + keyword.toLowerCase();
    redisTemplate.delete(key);
    redisTemplate.opsForList().rightPushAll(key, suggestions);
    redisTemplate.expire(key, Duration.ofMinutes(10));
  }

  public List<String> getCachedAutocomplete(String keyword) {
    String key = PREFIX_AUTOCOMPLETE + keyword.toLowerCase();
    return redisTemplate.opsForList().range(key, 0, -1);
  }

  public boolean hasAutocompleteCache(String keyword) {
    String key = PREFIX_AUTOCOMPLETE + keyword.toLowerCase();
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }

  // ✅ [2] 메인 인기/할인 게임 캐시 (String JSON 형태)
  public void cacheMainGames(String type, String jsonGameList) {
    String key = PREFIX_MAIN + type; // type = "popular" or "discount"
    redisTemplate.opsForValue().set(key, jsonGameList, Duration.ofMinutes(10));
  }

  public String getCachedMainGames(String type) {
    String key = PREFIX_MAIN + type;
    return redisTemplate.opsForValue().get(key);
  }

  public boolean hasMainGamesCache(String type) {
    String key = PREFIX_MAIN + type;
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }
}
