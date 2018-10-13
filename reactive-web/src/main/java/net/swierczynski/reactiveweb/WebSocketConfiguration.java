package net.swierczynski.reactiveweb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;

/**
 * Date: 12/10/2018 at 12:57
 *
 * @author Marcin Świerczyński
 */
@Configuration
public class WebSocketConfiguration {

    @Bean
    WebSocketHandlerAdapter webSocketHandlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    @Bean
    WebSocketHandler webSocketHandler() {
        return session -> {
            final Flux<WebSocketMessage> greetingFlux = Flux
                    .<Greeting>generate(greetingSynchronousSink -> greetingSynchronousSink.next(new Greeting("Hello, world @ " + Instant.now().toString())))
                    .map(greeting -> session.textMessage(greeting.getText()))
                    .delayElements(Duration.ofSeconds(1))
                    .doFinally(signalType -> System.out.println("Goodbye."));

            return session.send(greetingFlux);
        };
    }

    @Bean
    HandlerMapping handlerMapping() {
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setUrlMap(Collections.singletonMap("/ws/hello", webSocketHandler()));
        handlerMapping.setOrder(10);
        return handlerMapping;
    }

}
