package com.exploringcn.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("reactive")
public class ReactiveController {

    // this returns a flux of integers (as a JSON) with a delay of 1 second in-between
    @GetMapping("/flux")
    public Flux<Integer> returnNumbersFlux(){
        return Flux.just(1,2,3,4,5)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

    // this returns a flux of integers (as a stream) with a delay of 1 second in-between
    @GetMapping(value = "/flux-stream", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> returnNumbersFluxStream(){
        return Flux.just(6,7,8,9)
                .delayElements(Duration.ofSeconds(1))
                .log();
    }

}
