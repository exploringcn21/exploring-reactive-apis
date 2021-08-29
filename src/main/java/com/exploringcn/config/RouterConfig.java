package com.exploringcn.config;

import com.exploringcn.handler.ReactiveHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import javax.print.attribute.standard.Media;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterConfig {

    @Bean
    public RouterFunction<ServerResponse> route(ReactiveHandler handler){
        return RouterFunctions
                .route(GET("/reactive/functional/flux").and(accept(MediaType.APPLICATION_JSON)), handler::flux)
                .andRoute(GET("/reactive/functional/mono").and(accept(MediaType.APPLICATION_JSON)), handler::mono);
    }
}
