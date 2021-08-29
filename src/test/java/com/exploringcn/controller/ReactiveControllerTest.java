package com.exploringcn.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import javax.print.attribute.standard.Media;

import static org.junit.jupiter.api.Assertions.*;

@WebFluxTest
@AutoConfigureWebTestClient(timeout = "36000")
@DirtiesContext
class ReactiveControllerTest {

    @Autowired
    WebTestClient client;


    // test the elements returned as a flux
    @Test
    public void testFluxApproach1(){
        Flux<Integer> numbersFlux = client.get().uri("/reactive/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();


        StepVerifier.create(numbersFlux)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .expectNext(5)
                .verifyComplete();
    }


    // test the elements returned as a infinite flux
    @Test
    public void testFluxInfiniteStream(){
        Flux<Long> fluxStream = client.get().uri("/reactive/flux-infinite-stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(fluxStream)
                .expectSubscription()
                .expectNext(0L)
                .expectNext(1L)
                .expectNext(2L)
                .expectNext(3L)
                .thenCancel()   // cancels subscription to stop flux from emitting further
                .verify();
    }


    // test a Mono
    @Test
    public void testMono(){
        Integer expected = 1;

        client.get().uri("/reactive/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((element) -> assertEquals(expected, element.getResponseBody()));
    }

}