package com.exploringcn.repository;

import com.exploringcn.document.Item;
import com.mongodb.reactivestreams.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongoConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.process.builder.ImmutableContainer;
import de.flapdoodle.embed.process.config.IExecutableProcessConfig;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> listOfItems = List.of(new Item(null, "Samsung", 400.0),
            new Item(null, "LG", 420.0),
            new Item(null, "Apple", 299.0),
            new Item(null, "OnePlus", 149.99));

    @BeforeEach
    public void setUp(){
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(listOfItems))
                .flatMap(itemReactiveRepository::save)  // every save operation on an element, returns a Publisher. We need to flatten it before further processing
                .doOnNext((item) -> System.out.println("Inserted item: " + item))
                .blockLast();   // wait till items have been persisted so that the setup can be tested properly. Use ONLY in test cases
    }

    @Test
    public void getAllItems(){
        Flux<Item> fluxOfItems = itemReactiveRepository.findAll();

        StepVerifier.create(fluxOfItems)
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

}