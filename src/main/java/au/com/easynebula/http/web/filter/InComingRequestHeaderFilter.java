package au.com.easynebula.http.web.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.List.of;
import static java.util.UUID.randomUUID;

@Component
@Order(3)
public class InComingRequestHeaderFilter implements WebFilter {

	@Value("#{'${easynebula.incoming.request.headers}'.split(',')}")
	private List<String> incomingRequestHeaderKeys;

	@Override
	public Mono<Void> filter(final ServerWebExchange serverWebExchange, final WebFilterChain webFilterChain) {
		serverWebExchange.getResponse().beforeCommit(() -> addContextToHttpResponseHeaders(serverWebExchange.getResponse()));

		final Map<String, String> headerMap = new HashMap<>();
		return webFilterChain.filter(serverWebExchange).subscriberContext(context -> {
			incomingRequestHeaderKeys.forEach(headerKey -> headerMap.put(headerKey, getHeaderValue(serverWebExchange, headerKey)));
			context = context.put("headers", headerMap);
			return context;
		});
	}

	private String getHeaderValue(final ServerWebExchange serverWebExchange, final String headerKey) {
		return serverWebExchange.getRequest().getHeaders().getOrDefault(headerKey, of(randomUUID().toString())).get(0);
	}

	private Mono<Void> addContextToHttpResponseHeaders(final ServerHttpResponse res) {
		return Mono.subscriberContext().doOnNext(ctx -> {
			if (!ctx.hasKey("headers")) {
				return;
			}
			final HttpHeaders headers = res.getHeaders();
			ctx.<Map<String, String>>get("headers").forEach(headers::add);
		}).then();
	}
}