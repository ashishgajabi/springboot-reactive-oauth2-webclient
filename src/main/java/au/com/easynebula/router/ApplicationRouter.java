package au.com.easynebula.router;

import au.com.easynebula.handler.billing.BillingReportHandler;
import au.com.easynebula.handler.test.TestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.nest;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ApplicationRouter {

	@Bean
	public RouterFunction<ServerResponse> billingReportRouterFunction(final BillingReportHandler billingReportHandler) {
		return nest(path("billing").and(accept(APPLICATION_JSON)),
						route((GET("/")), billingReportHandler::billingReportDetails)
								.andRoute(GET("/{id}"), billingReportHandler::findOne));
	}

	@Bean
	public RouterFunction<ServerResponse> justAnotherRouterFunction(final TestHandler testHandler) {
		return route(GET("/test").and(accept(APPLICATION_JSON)), testHandler::test);
	}
}
