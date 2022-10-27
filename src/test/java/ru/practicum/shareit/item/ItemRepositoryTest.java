package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;


    private final Pageable pageable = PageRequest.of(0, 10);

    private final User owner = new User(1L, "user", "user@user.com");

    private final Item item = new Item(1L, "Дрель",
            "Простая дрель", Boolean.TRUE, owner.getId(), null);

    private final Item item2 = new Item(2L, "Отвертка",
            "Аккумуляторная отвертка", Boolean.TRUE, owner.getId(), null);

    @BeforeEach
    void setUp() {
        userRepository.save(owner);
        itemRepository.save(item);
        itemRepository.save(item2);
    }

    @AfterEach
    void resetSetUp() {
        em.clear();
    }

    @Test
    void searchTest() {
        List<Item> items = itemRepository.search("%Дрель%", pageable).toList();
        assertEquals(1, items.size());
        assertEquals(item, items.get(0));
    }
}
