package com.exploringcn.basics;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

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


    // 02 - Subscribe to a Flux of elements, read each emitted element & verify that it exits with any error
    @Test
    public void testFluxEventsAreReadInOrderWithError(){
        // create Flux of names and an exception
        Flux<String> namesFlux = Flux.just("Adam", "Ben", "Claire", "Dorset", "Elan")
                .concatWith(Flux.error(new RuntimeException("Exceptional Event")));


        // verify that an exception event is emitted from the flux
        StepVerifier.create(namesFlux)
                .expectNext("Adam") //  Expect elements in order
                .expectNext("Ben")
                .expectNext("Claire")
                .expectNext("Dorset")
                .expectNext("Elan")
                .expectError(RuntimeException.class)    // verify that RuntimeException is thrown by flux
                .verify();  // This will actually trigger the flux to start emitting. Since we are expecting an error, there is no onComplete event.
    }

    // 03 - Subscribe to a Flux of elements, and verify its count
    @Test
    public void testFluxEventsCount(){

        // Create Flux of elements
        Flux<String> namesFlux = Flux.just("Adam", "Ben", "Claire", "Dorset", "Elan");

        // Subscribe to the flux and test that elements are received in order
        StepVerifier.create(namesFlux)  // Prepare the StepVerifier for testing. It will subscribe to the flux passed in.
                //.expectNextCount(5) // this triggers the onComplete event. We cannot expect an onNext after this has run.
                .expectNext("Adam")
                .expectNextCount(4) // count of the number of elements in the flux
                .verifyComplete();  // verify that all flux elements have been read without error. This will also trigger the flux to start emitting.

    }

    // 04 - Create a Flux from an ArrayList<String> i.e. an Iterable type
    @Test
    public void testFluxFromArrayList(){
        List<String> listOfNames = Arrays.asList("Adam", "Ben", "Claire");

        // create flux from above list
        Flux<String> namesFlux = Flux.fromIterable(listOfNames);

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Ben", "Claire")    // Expect elements in order
                .verifyComplete();
    }

    // 05 - Create a Flux from a String[] array
    @Test
    public void testFluxFromArray(){
        String[] arrayOfNames = new String[]{"Adam", "Ben", "Claire"};

        // create flux from above array
        Flux<String> namesFlux = Flux.fromArray(arrayOfNames);

        StepVerifier.create(namesFlux)
                .expectNext("Adam", "Ben", "Claire")
                .verifyComplete();
    }

    // 06 - Create a Flux from a Stream
    @Test
    public void testFluxFromStream(){
        Stream<String> streamOfNames = Stream.of("Adam", "Ben", "Claire")
                                            .map(String::toUpperCase);  // transform to UPPERCASE

        Flux<String> namesFlux = Flux.fromStream(streamOfNames);

        StepVerifier.create(namesFlux)
                .expectNext("ADAM", "BEN", "CLAIRE")    // verify that elements are in UPPERCASE
                .verifyComplete();

    }

    // 07 - Create a Flux with a range of integers
    @Test
    public void testFluxIntegersRange(){
        Flux<Integer> numbersFlux = Flux.range(1, 10);  // 10 consecutive numbers starting with 1

        StepVerifier.create(numbersFlux)
                .expectNext(1, 2, 3, 4, 5, 6, 7, 8, 9, 10)
                .verifyComplete();
    }

}
