package au.com.easynebula.handler.test;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class TestHandler {

	public Mono<ServerResponse> test(final ServerRequest serverRequest) {
		return ServerResponse
				.ok()
				.contentType(APPLICATION_JSON)
				.body(fromValue("Test Message"));
	}
}
