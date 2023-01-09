package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.transaction.AfterTransaction;
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

    private User owner;
    private Item item1;
    private Item item2;
    private Item item3;
    private Item item4;

    @BeforeEach
    void fillData() {
        owner = new User();
        owner.setName("user");
        owner.setEmail("user@user.com");
        em.persist(owner);

        item1 = new Item();
        item1.setName("Дрель");
        item1.setDescription("Простая дрель");
        item1.setAvailable(true);
        item1.setOwnerId(owner.getId());

        item2 = new Item();
        item2.setName("Отвертка");
        item2.setDescription("Крестовая отвертка");
        item2.setAvailable(true);
        item2.setOwnerId(owner.getId());

        item3 = new Item();
        item3.setName("Бензопила");
        item3.setDescription("Дружба");
        item3.setAvailable(true);
        item3.setOwnerId(owner.getId());

        item4 = new Item();
        item4.setName("Перфоратор");
        item4.setDescription("Перфоратор");
        item4.setAvailable(false);
        item4.setOwnerId(owner.getId());

        em.persist(item1);
        em.persist(item2);
        em.persist(item3);
        em.persist(item4);
    }

    @AfterTransaction
    public void showCountAfterTransaction() {
        System.out.println("Item count after tx: " + itemRepository.count());
    }

    @Test
    void testSearchInName() {
        List<Item> result = itemRepository.search("Дрель", pageable).toList();

        assertEquals(1, result.size());
        assertEquals(item1, result.get(0));
    }

    @Test
    void testSearchInDescription() {
        List<Item> result = itemRepository.search("Дружба", pageable).toList();

        assertEquals(1, result.size());
        assertEquals(item3, result.get(0));
    }

    @Test
    void testSearchAndSkipNotAvailable() {
        List<Item> result = itemRepository.search("Перфоратор", pageable).toList();

        assertEquals(0, result.size());
    }
}
