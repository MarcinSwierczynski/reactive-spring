package net.swierczynski.testservice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.Duration;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestServiceApplicationTests {

    private WebTestClient webTestClient;

    @Autowired
    private ApplicationContext applicationContext;

    private PublisherService publisherService = new PublisherService();

    @Before
    public void before() {
        this.webTestClient = WebTestClient
                .bindToApplicationContext(this.applicationContext)
                .configureClient()
                .baseUrl("http://localhost:8080")
                .build();
    }

    @Test
    public void shouldGreet() {
        this.webTestClient
                .get()
                .uri("/hi")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    public void shouldReturnElements() {
        StepVerifier.withVirtualTime(() -> publisherService.publish().take(10).collectList())
                .thenAwait(Duration.ofHours(1))
                .consumeNextWith(list -> Assert.assertEquals(10, list.size()))
                .verifyComplete();
    }

}
