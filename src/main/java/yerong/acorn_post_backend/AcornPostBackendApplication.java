package yerong.acorn_post_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AcornPostBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(AcornPostBackendApplication.class, args);
	}

}
