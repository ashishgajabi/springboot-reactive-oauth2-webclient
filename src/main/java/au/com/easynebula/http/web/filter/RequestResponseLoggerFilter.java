package au.com.easynebula.http.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(2)
public class RequestResponseLoggerFilter implements WebFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseLoggerFilter.class);

	@Override
	public Mono<Void> filter(final ServerWebExchange serverWebExchange, final WebFilterChain webFilterChain) {
		final ServerHttpRequest request = serverWebExchange.getRequest();
		final ServerHttpResponse response = serverWebExchange.getResponse();

		LOGGER.info("easynebula: received request [{}] with URI: [{}] with request headers [{}] ",
				request.getMethod(), request.getURI().getPath(), request.getHeaders().toSingleValueMap());

		response.beforeCommit(() -> {
			LOGGER.info("easynebula: sending response with status [{}] response headers [{}]",
					response.getStatusCode(), response.getHeaders().toSingleValueMap());
			return Mono.empty();
		});

		return webFilterChain.filter(serverWebExchange);
	}
}