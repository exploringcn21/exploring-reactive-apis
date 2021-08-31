package com.exploringcn.init;

import com.exploringcn.document.Item;
import com.exploringcn.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
@Slf4j
public class ItemDatabaseInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) {

        setupDatabase();
    }

    private void setupDatabase() {

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(supplyData()))
                .flatMap((item) -> itemReactiveRepository.save(item))
                .subscribe((item) -> log.info("ITEM {} INSERTED SUCCESSFULLY.", item));
    }

    private List<Item> supplyData() {
        return List.of(Item.builder().id(null).description("SAMSUNG TV").price(399.99).build(),
                Item.builder().id(null).description("LG TV").price(329.99).build(),
                Item.builder().id(null).description("Apple Watch").price(349.99).build(),
                Item.builder().id("abc123").description("Beats Headphones").price(19.99).build());
    }
}
