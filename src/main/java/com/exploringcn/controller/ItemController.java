package com.exploringcn.controller;

import com.exploringcn.constants.ItemConstants;
import com.exploringcn.document.Item;
import com.exploringcn.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.exploringcn.constants.ItemConstants.ITEM_ENDPOINT_V1;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_ENDPOINT_V1)
    public Flux<Item> getAllItems(){
        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_ENDPOINT_V1+"/{itemId}")
    public Mono<ResponseEntity<Item>> getItem(@PathVariable String itemId){
        return itemReactiveRepository.findById(itemId)
                .map((item) -> new ResponseEntity<>(item, HttpStatus.OK))   // return response wrapped in Mono along with 200 status
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));    // return NOT FOUND response wrapped in Mono
    }
}
