package com.adg.api.department.Accounting.service;

import com.adg.api.department.Accounting.enums.MisaEndpoint;
import com.google.common.collect.ImmutableMap;
import com.merlin.asset.core.utils.JsonUtils;
import com.merlin.asset.core.utils.MapUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.util.retry.Retry;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Minh-Luan H. Phan
 * Created on: 2022.03.09 11:25
 */
@Component
public class MisaWebClientService {

    @Value("${misa.client-id}")
    private String clientId;

    @Value("${misa.client-secret}")
    private String clientSecret;

    @Value("${misa.base-url}")
    private String baseUrl;

    @Value("${misa.api-timeout}")
    private int apiTimeOut;

    private WebClient webClient;
    private WebClient authWebClient;

    public MisaWebClientService() {

    }

    @PostConstruct
    public void refreshBearerToken() {
        HttpClient httpClient = HttpClient
                .create()
                .responseTimeout(Duration.ofSeconds(apiTimeOut));

        if (this.authWebClient == null) {
            this.authWebClient = WebClient.builder()
                    .clientConnector(new ReactorClientHttpConnector(httpClient))
                    .baseUrl(baseUrl)
                    .build();
        }

        String bearerToken = "Bearer " + retrieveBearerToken();

        this.webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(baseUrl)
                .defaultHeader("Authorization", bearerToken)
                .build();
    }

     public String retrieveBearerToken() {
        Mono<Map<String, Object>> result = this.authWebClient
                .post()
                .uri(MisaEndpoint.ACCOUNT.uri)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(MapUtils.ImmutableMap()
                        .put("client_id", clientId)
                        .put("client_secret", clientSecret)
                        .build()), Map.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(10)))
                ;

        Map<String, Object> response = result.block();

        return MapUtils.getString(response, "data");
    }

    // TODO: handle re run retrieveBearerToken()
    public Map<String, Object> get(Function<UriBuilder, URI> uriFunction) {
        this.refreshBearerToken();
        Mono<Map<String, Object>> result = this.webClient
                .get()
                .uri(uriFunction)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .retryWhen(Retry.backoff(5, Duration.ofSeconds(30)));
        return result.block();
    }

    private Function<? super Throwable, ? extends Mono<? extends Map<String, Object>>> onCallingApiError() {
        return (exception -> {
            ImmutableMap.Builder<String, Object> responseBuilder = MapUtils.ImmutableMap();
            responseBuilder
                    .put("exception_message", Optional.ofNullable(exception.getMessage()).orElse("Exception is empty"))
                    .put("exception_class", exception.getClass())
            ;

            if (exception instanceof WebClientResponseException) {
                return (Mono<? extends Map<String, Object>>) this.handleWebClientResponseException(responseBuilder);
            } else {
                return (Mono<? extends Map<String, Object>>) this.handleOtherException(responseBuilder);
            }

        });
    }

    private Function<WebClientResponseException, Mono<? extends Map<String, Object>>> handleWebClientResponseException(
            ImmutableMap.Builder<String, Object> responseBuilder
    ) {
        return exception -> {
            responseBuilder
                    .put("response_body", JsonUtils.fromJson(exception.getResponseBodyAsString(), JsonUtils.TYPE_TOKEN.MAP_STRING_OBJECT.type))
                    .put("status_code", exception.getStatusCode().value())
                    .put("exception_message", Optional.ofNullable(exception.getMessage()).orElse("Exception is empty"))
                    .put("exception_class", exception.getClass())
            ;

            if (exception.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                responseBuilder.put("is_unauthenticated", true);

                this.refreshBearerToken();

            } else {
                responseBuilder.put("is_unauthenticated", false);
            }
            return Mono.just(responseBuilder.build());
        };
    }

    private Function<? super Throwable, Mono<Map<String, Object>>> handleOtherException(
            ImmutableMap.Builder<String, Object> responseBuilder
    ) {
        return throwable  -> Mono.just(responseBuilder.build());
    }

    public Map<String, Object> post(Function<UriBuilder, URI> uriFunction, Map<String, Object> body) {
        this.refreshBearerToken();

        Mono<Map<String, Object>> result = this.webClient
                .post()
                .uri(uriFunction)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), Map.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
        return result.block();
    }

    public Map<String, Object> put(Function<UriBuilder, URI> uriFunction, Map<String, Object> body) {
        this.refreshBearerToken();

        Mono<Map<String, Object>> result = this.webClient
                .put()
                .uri(uriFunction)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), Map.class)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {});
        return result.block();
    }

}
