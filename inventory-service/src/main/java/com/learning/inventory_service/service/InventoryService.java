/*
@SneakyThrows is a Lombok annotation that lets you avoid writing try-catch or throws for checked exceptions.
 */

package com.learning.inventory_service.service;

import com.learning.inventory_service.dto.InventoryResponse;
import com.learning.inventory_service.model.Inventory;
import com.learning.inventory_service.repository.InventoryRepository;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    @SneakyThrows
    public List<InventoryResponse> IsInStock(List<String> skuCode){
        log.info("Wait started");
        Thread.sleep(10000);
        log.info("Wait Ends");
        return inventoryRepository.findBySkuCodeIn(skuCode).stream()
                .map(inventory ->
                    InventoryResponse.builder()
                            .skuCode(inventory.getSkuCode())
                            .isInStock(inventory.getQuantity() > 0)
                            .build()
                ).toList();
    }
}
