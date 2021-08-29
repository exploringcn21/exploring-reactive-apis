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
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@DirtiesContext
class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> listOfItems = List.of(new Item(null, "Samsung", 400.0),
            new Item(null, "LG", 420.0),
            new Item(null, "Apple", 299.0),
            new Item(null, "OnePlus", 149.99),
            new Item("abc123", "Nokia", 149.99));

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
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemByID(){
        Mono<Item> itemMono = itemReactiveRepository.findById("abc123");

        StepVerifier.create(itemMono)
                .expectSubscription()
                .expectNextMatches((item) -> "Nokia".equals(item.getDescription()))
                .verifyComplete();
    }

    @Test
    public void getItemByDescription(){
        Flux<Item> itemMono = itemReactiveRepository.findByDescription("OnePlus");

        StepVerifier.create(itemMono)
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem(){
        Item item = Item.builder()
                .id(null)
                .description("Google Mini Home")
                .price(30.00)
                .build();


        Mono<Item> itemMono = itemReactiveRepository.save(item);

        StepVerifier.create(itemMono)
                .expectSubscription()
                .expectNextMatches((i) -> i.getId() != null && "Google Mini Home".equals(i.getDescription()))
                .verifyComplete();
    }

    @Test
    public void updateItem(){
        Double newPrice = 159.99;

        Flux<Item> itemFlux = itemReactiveRepository.findByDescription("OnePlus")
                .map(item -> {
                    item.setPrice(newPrice);
                    return item;
                })  // Flux<Item>
                .flatMap(itemReactiveRepository::save); // save returns Flux<Flux<Item>> therefore need to flatten

        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextMatches(item -> item.getPrice() == 159.99)
                .verifyComplete();
    }

    @Test
    public void deleteItem(){
        Mono<Void> deletedItem = itemReactiveRepository.findById("abc123")
                .map(Item::getId)
                .flatMap(itemReactiveRepository::deleteById);   // flatten response from Mono<Mono<Item>> to Mono<Item>

        StepVerifier.create(deletedItem.log("Item Mono delete: "))
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(itemReactiveRepository.findAll().log("Item FindAll: "))
                .expectNextCount(4)
                .verifyComplete();
    }

}