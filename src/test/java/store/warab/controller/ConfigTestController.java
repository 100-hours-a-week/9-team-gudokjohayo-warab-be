// package store.warab.controller;
//
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RestController;
//
// @SpringBootTest
// @RestController
// @RequestMapping("/config")
// public class ConfigTestController {
//
//    @Value("${SPRING_DATASOURCE_URL}")
//    private String datasourceUrl;
//
//    @Value("${OAUTH_KAKAO_CLIENT_ID}")
//    private String kakaoClientId;
//
//    @GetMapping
//    public String checkConfig() {
//        return "DB URL: " + datasourceUrl + " | " + kakaoClientId + kakaoClientId;
//    }
// }
