package com.exploringcn.basics;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoWorkoutTest {


    // 01 - Subscribe to a Flux of elements, read each emitted element & verify that it completes without any error
    @Test
    public void testFluxEventsAreReadInOrderWithoutError(){

        // Create Flux of elements
        Flux<String> namesFlux = Flux.just("Adam", "Ben", "Claire", "Dorset", "Elan");

        // Subscribe to the flux and test that elements are received in order
        StepVerifier.create(namesFlux)  // Prepare the StepVerifier for testing. It will subscribe to the flux passed in.
                .expectNext("Adam") //  Expect elements in order
                .expectNext("Ben")
                .expectNext("Claire")
                .expectNext("Dorset")   // try commenting this out and observing the test fail
                .expectNext("Elan")
                .verifyComplete();  // verify that all flux elements have been read without error. This will also trigger the flux to start emitting.

    }

}
