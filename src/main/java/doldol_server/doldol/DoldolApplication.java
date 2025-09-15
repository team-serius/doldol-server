package doldol_server.doldol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class DoldolApplication {

	public static void main(String[] args) {
		SpringApplication.run(DoldolApplication.class, args);
	}

}
