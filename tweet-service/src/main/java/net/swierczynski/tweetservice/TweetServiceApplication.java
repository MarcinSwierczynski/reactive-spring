package net.swierczynski.tweetservice;

import akka.actor.ActorSystem;
import akka.japi.function.Function;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Source;
import akka.stream.scaladsl.Sink;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.reactivestreams.Publisher;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

interface TweetRepository extends ReactiveMongoRepository<Tweet, String> {
}

@SpringBootApplication
public class TweetServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TweetServiceApplication.class, args);
    }

    @Bean
    ApplicationRunner applicationRunner(TweetRepository repository) {
        return args -> {
            Author marcin = new Author("marcin"), aga = new Author("aga");

            final Flux<Tweet> tweetFlux = Flux.just(
                    new Tweet("#java wow!", marcin),
                    new Tweet("#scala wow!", aga)
            );

            repository.deleteAll()
                    .thenMany(repository.saveAll(tweetFlux))
                    .thenMany(repository.findAll())
                    .subscribe(System.out::println);
        };
    }

    @Bean
    RouterFunction<ServerResponse> routes(TweetService tweetService) {
        return route(GET("/tweets"), req -> ok().body(tweetService.getAllTweets(), Tweet.class))
                .andRoute(GET("/hashtags"), req -> ok().body(tweetService.getAllHashTags(), HashTag.class));
    }
}

@Configuration
class AkkaConfiguration {

    @Bean
    ActorSystem actorSystem() {
        return ActorSystem.create("bootfil-akka-stream");
    }

    @Bean
    ActorMaterializer actorMaterializer() {
        return ActorMaterializer.create(this.actorSystem());
    }

}

@Service
class TweetService {
    private final TweetRepository repository;
    private final ActorMaterializer actorMaterializer;

    TweetService(final TweetRepository repository, final ActorMaterializer actorMaterializer) {
        this.repository = repository;
        this.actorMaterializer = actorMaterializer;
    }

    Publisher<Tweet> getAllTweets() {
        return this.repository.findAll();
    }

    Publisher<HashTag> getAllHashTags() {
        return Source
                .fromPublisher(getAllTweets())
                .map(Tweet::getHashTags)
                .reduce(this::join)
                .mapConcat((Function<Set<HashTag>, ? extends Iterable<HashTag>>) hashTags -> hashTags)
                .runWith(Sink.asPublisher(true), this.actorMaterializer);
    }

    private <T> Set<T> join(Set<T> a, Set<T> b) {
        Set<T> set = new HashSet<>();
        set.addAll(a);
        set.addAll(b);
        return set;
    }

}

@Document
@AllArgsConstructor
@Data
class HashTag {

    @Id
    private String id;
}

@Document
@AllArgsConstructor
@Data
class Author {

    @Id
    private String handle;
}

@Document
@AllArgsConstructor
@Data
class Tweet {

    @Id
    private String id;
    private String text;
    private Author author;

    public Tweet(final String text, final Author author) {
        this.text = text;
        this.author = author;
    }

    public Set<HashTag> getHashTags() {
        return Arrays.stream(this.text.split(" "))
                .filter(t -> t.startsWith("#"))
                .map(word -> new HashTag(word.replaceAll("[^#\\w+]", "").toLowerCase()))
                .collect(Collectors.toSet());
    }

}
