package au.com.easynebula;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveUserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = ReactiveUserDetailsServiceAutoConfiguration.class)
public class EasyNebulaOauth2APIGatewayReactiveApplication {

	public static void main(String[] args) {
		SpringApplication.run(EasyNebulaOauth2APIGatewayReactiveApplication.class, args);
	}
}