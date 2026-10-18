package com.jqdom.curriculum.ch25.inventory;

public interface InventoryService {

    /** 재고를 확보한다. 부족하면 {@link OutOfStockException}. */
    void reserve(String sku, int quantity);

    /** 확보했던 재고를 되돌린다(보상). */
    void release(String sku, int quantity);
}
