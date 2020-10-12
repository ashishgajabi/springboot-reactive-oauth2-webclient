package au.com.easynebula.http.webclient;

import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import static io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static reactor.core.publisher.Mono.just;

@Component
public class ApplicationWebClient {
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationWebClient.class);

	@Value("${easynebula.apigw.baseurl}")
	private String serverBaseUrl;

	@Bean
	public WebClient webClient(final ReactiveClientRegistrationRepository clientRegistrations,
	                           final ServerOAuth2AuthorizedClientRepository clientRepository,
	                           final ExchangeFilterFunction requestHeaderExchangeFilter) {
		final ServerOAuth2AuthorizedClientExchangeFilterFunction oauth =
				new ServerOAuth2AuthorizedClientExchangeFilterFunction(clientRegistrations, clientRepository);
		oauth.setDefaultClientRegistrationId("easynebula");

		return WebClient.builder()
				.filter(oauth)
				.filter(requestHeaderExchangeFilter)
				.filter(loggingRequestExchangeFilterFunction())
				.filter(loggingResponseExchangeFilterFunction())
				.defaultHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
				.clientConnector(new ReactorClientHttpConnector(getTcpClient()))
				.baseUrl(serverBaseUrl)
				.build();
	}

	private ExchangeFilterFunction loggingRequestExchangeFilterFunction() {
		return ExchangeFilterFunction.ofRequestProcessor(clientRequest -> {
				LOGGER.info("Sending [{}] request to URL [{}] with request headers [{}]",
						clientRequest.method(), clientRequest.url(), clientRequest.headers().toSingleValueMap());
			return just(clientRequest);
			});
	}

	private ExchangeFilterFunction loggingResponseExchangeFilterFunction() {
		return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
			LOGGER.info("Received response with status [{}] with response headers [{}]",
						clientResponse.statusCode(), clientResponse.headers().asHttpHeaders().toSingleValueMap());
			return just(clientResponse);
			});
	}

	private HttpClient getTcpClient() {
		return HttpClient.create()
				.tcpConfiguration(tcpClient -> {
					tcpClient = tcpClient.option(CONNECT_TIMEOUT_MILLIS, 5000);
					tcpClient = tcpClient.doOnConnected(conn -> {
							conn.addHandlerLast(new ReadTimeoutHandler(5000, MILLISECONDS));
							conn.addHandlerLast(new WriteTimeoutHandler(5000, MILLISECONDS));
					});
					return tcpClient;
				});
	}

	@Bean
	public SecurityWebFilterChain configure(ServerHttpSecurity http) {
		return http.oauth2Client().and().build();
	}
}