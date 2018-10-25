package net.swierczynski.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.handler.GenericHandler;
import org.springframework.integration.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.Duration;
import java.util.Date;

@SpringBootApplication
public class ReactiveSpringIntegration {

//    @Bean
    IntegrationFlow flow() {
        final Flux<Date> dates =
                Flux.<Date>generate(sink -> sink.next(new Date())).delayElements(Duration.ofSeconds(1));

        return IntegrationFlows
                .from(dates.map(d -> MessageBuilder.withPayload(d).build()))
                .handle((GenericHandler<Date>) (payload, headers) -> {
                    System.out.println("Date is " + payload.toInstant().toString());
                    headers.forEach((k, v) -> System.out.println(k + "=" + v));
                    return null;
                })
                .handle(Files.outboundAdapter(new File("/Users/marcin/Desktop")))
                .get();
    }
}
