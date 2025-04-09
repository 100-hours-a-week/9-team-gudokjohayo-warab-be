package store.warab.service;

import java.time.Duration;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class AutocompleteCacheService {

  private final StringRedisTemplate redisTemplate;
  private final String KEY_PREFIX = "autocomplete:";

  public AutocompleteCacheService(StringRedisTemplate redisTemplate) {
    this.redisTemplate = redisTemplate;
  }

  public void cacheAutocomplete(String keyword, List<String> suggestions) {
    String key = KEY_PREFIX + keyword.toLowerCase();
    redisTemplate.delete(key); // 중복 방지
    redisTemplate.opsForList().rightPushAll(key, suggestions);
    redisTemplate.expire(key, Duration.ofMinutes(10)); // 10분 캐시
  }

  public List<String> getCachedAutocomplete(String keyword) {
    String key = KEY_PREFIX + keyword.toLowerCase();
    return redisTemplate.opsForList().range(key, 0, -1);
  }

  public boolean hasCache(String keyword) {
    String key = KEY_PREFIX + keyword.toLowerCase();
    return Boolean.TRUE.equals(redisTemplate.hasKey(key));
  }
}
