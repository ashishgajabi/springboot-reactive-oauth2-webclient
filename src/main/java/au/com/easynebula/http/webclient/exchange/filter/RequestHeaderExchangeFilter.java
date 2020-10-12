package au.com.easynebula.http.webclient.exchange.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

import static au.com.easynebula.utils.StringConstants.X_API_KEY;
import static org.springframework.web.reactive.function.client.ClientRequest.from;

@Component
public class RequestHeaderExchangeFilter implements ExchangeFilterFunction {

	@Value("${easynebula.apigw.clientAPIKey}")
	private String apiKey;

	@Value("#{'${easynebula.incoming.request.headers}'.split(',')}")
	private List<String> incomingRequestHeaderKeys;

	@Override
	public Mono<ClientResponse> filter(ClientRequest clientRequest, ExchangeFunction exchangeFunction) {
		return Mono.subscriberContext().flatMap(context -> {
			final Map<String, String> incomingHeaders = context.get("headers");
			final ClientRequest newRequest = from(clientRequest).headers(httpHeaders -> {
				httpHeaders.add(X_API_KEY, apiKey);
				incomingRequestHeaderKeys.forEach(headerKey -> httpHeaders.add(headerKey, incomingHeaders.get(headerKey)));
			}).build();
			return exchangeFunction.exchange(newRequest);
		});
	}
}