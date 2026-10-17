package com.devguna.curriculum.ch25.inventory;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class InMemoryInventoryService implements InventoryService {

    private final Map<String, Integer> stock = new HashMap<>(Map.of("BOOK", 5, "PEN", 0));

    @Override
    public synchronized void reserve(String sku, int quantity) {
        int current = stock.getOrDefault(sku, 0);
        if (current < quantity) {
            throw new OutOfStockException(sku);
        }
        stock.put(sku, current - quantity);
    }

    @Override
    public synchronized void release(String sku, int quantity) {
        stock.merge(sku, quantity, Integer::sum);
    }

    public synchronized int stockOf(String sku) {
        return stock.getOrDefault(sku, 0);
    }
}
