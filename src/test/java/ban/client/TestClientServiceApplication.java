package ban.client;

import org.springframework.boot.SpringApplication;

public class TestClientServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(ClientServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
