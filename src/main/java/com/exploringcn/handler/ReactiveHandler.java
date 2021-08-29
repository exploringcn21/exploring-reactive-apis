package com.exploringcn.handler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ReactiveHandler {

    public Mono<ServerResponse> flux(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Flux.just(1,2,3,4), Integer.class);   // specify the Publisher to write to the response and the type of each element in the Publisher
    }

    public Mono<ServerResponse> mono(ServerRequest request){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(1), Integer.class);
    }
}
