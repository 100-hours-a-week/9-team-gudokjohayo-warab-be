//package store.warab;
//
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//
//@SpringBootTest(
//    properties =
//        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // ✅ DB 자동설정 비활성화
//@ActiveProfiles("test") // ✅ test 프로파일을 활성화 (선택)
//class WarabApplicationTests {
//
//  @Test
//  void contextLoads() {} // 기본 컨텍스트 로딩 테스트
//}


package store.warab;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(
    properties =
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // ✅ DB 자동설정 비활성화
@ActiveProfiles("test") // ✅ test 프로파일을 활성화 (선택)
class WarabApplicationTests {

    @Test
    void contextLoads() {} // 기본 컨텍스트 로딩 테스트
}
