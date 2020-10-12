# springboot-reactive-oauth2-webclient

Sample Reactive SpringBoot application which uses **Netty with WebFlux**. It also makes reactive calls to downstream APIs 
using **API Gateway with client_credentials OAuth2 authorisation using webclient with timeout settings**. End to end flow is handled reactively non-blocking way. 

It uses webfliter to inject headers, to logs request/responses and uses **sleuth** as a way for logging specific custom headers along with trace and span ids in logs.
