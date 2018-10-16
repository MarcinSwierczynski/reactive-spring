package net.swierczynski.reactiveweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.*;

/**
 * @author Marcin Świerczyński
 */
@Configuration
public class FunctionalJavaConfiguration {

    private static Mono<ServerResponse> handle(ServerRequest req) {
        return ServerResponse
                .ok()
                .contentType(MediaType.TEXT_EVENT_STREAM)
                .body(Flux.<Greeting>generate(
                        sink -> sink.next(new Greeting("Hello, world @ " + Instant.now().toString())))
                        .delayElements(Duration.ofSeconds(5)), Greeting.class);
    }

    @Bean
    RouterFunction<ServerResponse> routes() {
        return route(GET("/frp/greetings"), req -> ServerResponse.ok().body(Flux.just("Hello, world!"), String.class))
                .andRoute(GET("/frp/sse"), FunctionalJavaConfiguration::handle);
    }

}
