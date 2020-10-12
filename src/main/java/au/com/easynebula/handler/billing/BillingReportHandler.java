package au.com.easynebula.handler.billing;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class BillingReportHandler {
	private static final Logger LOG = LoggerFactory.getLogger(BillingReportHandler.class);

	private final WebClient webClient;

	@Autowired
	public BillingReportHandler(final WebClient webClient) {
		this.webClient = webClient;
	}

	public Mono<ServerResponse> billingReportDetails(final ServerRequest serverRequest) {

		final Mono<JsonNode> billingReportMono = webClient.get()
				.uri("/v1/billing/reports/?reportType={reportType}", "testType")
				.header("test", "testHeader")// example to show per request headers
				.retrieve()
				.onStatus(HttpStatus::isError, clientResponse -> {
					LOG.error("Error while calling endpoint with status code {}", clientResponse.statusCode());
					throw new RuntimeException("Error while calling  accounts endpoint");
				})
				.bodyToMono(JsonNode.class)
				.flatMap(s -> {
					LOG.info("Received response with body [{}]", s);
					return Mono.just(s);
				})
				.doOnError(error -> LOG.error("Error signal detected", error));

		Mono<String> stringMono = billingReportMono
				.flatMap(jsonNode -> Mono.just(jsonNode.get("data").get("attributes").get(0).get("reportId").asText()))
				.flatMap(s ->
					webClient.get()
							.uri("/v1/billingdocs/reports/{reportId}", s)
							.header("clientId", ("64676747347132825178920147815047"))// example to show per request headers
							.retrieve().bodyToMono(String.class)
				);

		return ServerResponse
				.ok()
				.contentType(APPLICATION_JSON)
				.body(fromPublisher(stringMono, String.class));
	}

	public Mono<ServerResponse> findOne(final ServerRequest serverRequest) {
		return ServerResponse
				.ok()
				.contentType(APPLICATION_JSON)
				.body(fromValue("Id given in request was " + serverRequest.pathVariable("id")));
	}
}
