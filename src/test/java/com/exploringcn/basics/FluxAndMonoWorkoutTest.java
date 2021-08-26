package com.exploringcn.basics;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
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

    // 08 - Create a Flux of names and FILTER names that start with 'A'
    @Test
    public void testFilterNamesWithA(){
        Flux<String> namesWithAFlux = Flux.just("Arman", "Ayden", "Tony", "Florence", "Gil", "Ash", "Ackermann")
                                            .filter((item) -> item.startsWith("A"));    // filter names that start with A

        StepVerifier.create(namesWithAFlux)
                .expectNext("Arman", "Ayden", "Ash", "Ackermann")
                .verifyComplete();

    }

    // 09 - Create a Flux of names and FILTER names that start with 'A'
    @Test
    public void testFilterNamesWithAThatAreFiveLettersLong(){
        Flux<String> namesWithAFlux = Flux.just("Arman", "Ayden", "Tony", "Florence", "Gil", "Ash", "Ackermann")
                                            .filter((item) -> item.startsWith("A"))    // filter names that start with A
                                            .filter((item) -> item.length() == 5);  // filter names that are 5 letters long

        StepVerifier.create(namesWithAFlux)
                .expectNext("Arman", "Ayden")
                .verifyComplete();

    }

    // 10 - Create a Flux of integers and filter the even ones
    @Test
    public void testFilterEvenIntegers(){
        Flux<Integer> numbersFlux = Flux.range(-10, 21)
                .filter((number) -> number % 2 == 0);

        StepVerifier.create(numbersFlux)
                .expectNext(-10, -8, -6, -4, -2, 0, 2, 4, 6, 8, 10)
                .verifyComplete();
    }

    // 11 - Create a Flux of integers, find their squares and emit the flux THRICE again
    @Test
    public void testMapAndRepeatFlux(){
        Flux<Integer> numbersFlux = Flux.range(1,5)
                .map(n -> n*n)  // transform each emitted element from flux
                .repeat(3);  // repeat the above transformation 3 times more

        StepVerifier.create(numbersFlux)
                .expectNext(1,4,9,16,25)    // transform and produce flux
                .expectNext(1,4,9,16,25)    // repeat
                .expectNext(1,4,9,16,25)    // repeat
                .expectNext(1,4,9,16,25)    // repeat
                .verifyComplete();
    }

    // 12 - Create a Flux of english alphabets, pass them to a mock DB method that returns an aggregate response as Flux, then flatten and transform each element
    @Test
    public void testFlatMap(){
        Flux<String> lettersFlux = Flux.just("A","B","C")
                .flatMap(this::simulateDatabaseCall)    // flatten ((A1, A2, A3), (B1, B2, B3), (C1, C2, C3)) to (A1, A2, A3, B1, B2, B3, C1, C2, C3)
                .map(String::toLowerCase)   // transform to ("a1","a2","a3","b1","b2","b3","c1","c2","c3")
                .log(); // visualize processing


        StepVerifier.create(lettersFlux)
                .expectNext("a1","a2","a3","b1","b2","b3","c1","c2","c3")
                .verifyComplete();
    }

    // simulate a DB call that takes in an input and returns a Flux of elements
    private Flux<String> simulateDatabaseCall(String s) {
        try {
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Flux.just(s+"1", s+"2", s+"3");
    }

    // 13 - simulate an exception during processing of the flux but resume it by supplying a new flux
    @Test
    public void fluxExceptionHandlingWithOnErrorResume(){

        int number = 100;
        Flux<Integer> numbersFlux = Flux.just(66, 10 ,3, 2, 0, 20, 1, 1000)
                .filter((n) -> number % n == 0) // filter numbers from the flux that are not factors of 100
                .onErrorResume((e) -> Flux.just(1, 100))
                .log();

        StepVerifier.create(numbersFlux)
                .expectSubscription()
                .expectNext(10, 2)  // these get filtered successfully before "divide by zero exception" is raised
                .expectNext(1, 100)  // these get returned to pipeline due to error
                .verifyComplete();
    }

}
