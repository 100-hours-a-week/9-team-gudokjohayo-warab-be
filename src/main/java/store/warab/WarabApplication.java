package store.warab;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application-secret.properties")
public class WarabApplication {

  public static void main(String[] args) {
    SpringApplication.run(WarabApplication.class, args);
  }
}
