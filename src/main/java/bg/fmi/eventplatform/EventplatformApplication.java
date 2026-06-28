package bg.fmi.eventplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EventplatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventplatformApplication.class, args);
	}

}
