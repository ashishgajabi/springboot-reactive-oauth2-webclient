package au.com.easynebula.http.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Order(1)
public class ResponseTimeFilter implements WebFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseTimeFilter.class);

	@Override
	public Mono<Void> filter(final ServerWebExchange serverWebExchange, final WebFilterChain webFilterChain) {
		LOGGER.info("Started to serve request...[{}]", serverWebExchange.getRequest().getURI().getPath());

		final long startMillis = System.currentTimeMillis();

		return webFilterChain.filter(serverWebExchange)
				.doOnSuccess(aVoid -> LOGGER.info("Total time elapsed to process the request: [{} ms] for request [{}]",
						System.currentTimeMillis() - startMillis, serverWebExchange.getRequest().getURI().getPath()))
				.doOnError(aVoid -> LOGGER.info("Total time elapsed to process the request with error: [{} ms] for request [{}]",
						System.currentTimeMillis() - startMillis, serverWebExchange.getRequest().getURI().getPath()));
	}
}