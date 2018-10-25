package net.swierczynski.producer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@SpringBootApplication
@EnableBinding(Source.class)
public class ProducerApplication {

    private final Log log = LogFactory.getLog(getClass());

    public static void main(String[] args) {
        SpringApplication.run(ProducerApplication.class, args);
    }

    @Bean
    ApplicationRunner producer(Source source) {
        return args -> {
            int i = 0;
            while (true) {
                final Message<String> message = MessageBuilder.withPayload("Hello, #" + ++i).build();
                this.log.info("Sending " + message.getPayload());
                source.output().send(message);
                Thread.sleep(2000);
            }
        };
    }
}
