package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface RequestService {

    List<ItemRequest> getAllByRequestorId(Long requestorId);

    ItemRequest getById(Long id);

    ItemRequest add(ItemRequest itemRequest);

    ItemRequest update(ItemRequest itemRequest);

    void delete(Long id);

    Page<Item> getExistedForUserId(Long userId, Pageable pageable);

}
