package ru.otus.java.basic.http.server.app;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.util.*;

public class ItemsRepository {
    private List<Item> items;
    private static final Logger logger = LogManager.getLogger(ItemsRepository.class.getName());

    public List<Item> getItems() {
        return Collections.unmodifiableList(items);
    }

    public ItemsRepository() {
        this.items = new ArrayList<>(Arrays.asList(
                new Item(1L, "Milk", BigDecimal.valueOf(80)),
                new Item(2L, "Bread", BigDecimal.valueOf(32)),
                new Item(3L, "Cheese", BigDecimal.valueOf(320))
        ));
    }

    public Item add(Item item) {
        Long newId = items.stream().mapToLong(Item::getId).max().orElse(0L) + 1L;
        item.setId(newId);
        items.add(item);
        return item;
    }

    public boolean delete(Long id) {
        for (Item item : items) {
            if (Objects.equals(item.getId(), id)) {
                items.remove(item);
                logger.debug("Запись с id {} удалена из репозитория", id);
                return true;
            }
        }
        return false;
    }
}
