package net.swierczynski.reactiveweb;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.Instant;

/**
 * Date: 12/10/2018 at 11:54
 *
 * @author Marcin Świerczyński
 */
@RestController
public class GreetingsRestController {

    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Publisher<Greeting> sseGreetings() {
        return Flux.<Greeting>generate(sink -> sink.next(new Greeting("Hello, world @ " + Instant.now().toString())))
                .delayElements(Duration.ofSeconds(5));
    }

    @GetMapping("/greetings")
    Publisher<Greeting> greetings() {
        return Flux.<Greeting>generate(sink -> sink.next(new Greeting("Hello, world!"))).take(1000);
    }

}
