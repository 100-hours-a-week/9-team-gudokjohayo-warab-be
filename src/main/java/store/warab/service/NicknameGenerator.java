package store.warab.service;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import store.warab.repository.UserRepository;

@Service
public class NicknameGenerator {

  private final UserRepository userRepository;

  private static final List<String> ADJECTIVES =
      Arrays.asList(
          "멋진", "용감한", "지혜로운", "빠른", "느긋한", "상냥한", "거대한", "작은", "은밀한", "유쾌한", "강력한", "화려한", "빛나는",
          "다정한", "따뜻한", "차가운", "기묘한", "재밌는", "끈기있는", "똑똑한");

  private static final List<String> ANIMALS =
      Arrays.asList(
          "호랑이", "늑대", "여우", "사자", "곰", "토끼", "펭귄", "돌고래", "독수리", "참새", "개미", "거북이", "뱀", "코끼리",
          "부엉이", "팬더", "수달", "치타", "고양이", "개");

  private static final SecureRandom RANDOM = new SecureRandom();

  public NicknameGenerator(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public String generateUniqueNickname() {
    String nickname;
    do {
      nickname = generateRandomNickname();
    } while (userRepository.existsByNickname(nickname)); // 중복 확인

    return nickname;
  }

  private String generateRandomNickname() {
    String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
    String animal = ANIMALS.get(RANDOM.nextInt(ANIMALS.size()));
    int randomNumber = RANDOM.nextInt(9000) + 1000; // 1000 ~ 9999 사이의 랜덤 숫자 생성
    return adjective + animal + randomNumber;
  }
}
