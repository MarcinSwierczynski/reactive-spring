package net.swierczynski.uppercase;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.function.Function;

@SpringBootApplication
public class UppercaseApplication {

    public static void main(String[] args) {
        SpringApplication.run(UppercaseApplication.class, args);
    }

    @Bean
    Function<Input, Output> uppercase() {
        return incoming -> new Output(incoming.getValue().toUpperCase());
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Input {
    private String value;
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Output {
    private String value;
}