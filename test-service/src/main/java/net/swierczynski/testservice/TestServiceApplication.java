package net.swierczynski.testservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.ServerResponse.*;

@SpringBootApplication
public class TestServiceApplication {

    @Bean
    RouterFunction<ServerResponse> routes() {
        return RouterFunctions.route(GET("/hi"), req -> ok().body(Flux.just("Hello, world!"), String.class));
    }

    public static void main(String[] args) {
        SpringApplication.run(TestServiceApplication.class, args);
    }
}

@Component
class PublisherService {

    Flux<String> publish() {
        return Flux.<String>generate(sink -> sink.next("Hello @ " + Instant.now().toString()))
                .delayElements(Duration.ofSeconds(1));
    }

}
