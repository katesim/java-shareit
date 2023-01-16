package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;

import java.util.List;

public interface RequestService {

    List<ItemRequest> getAllByRequesterId(Long requesterId);

    ItemRequest getById(Long userId, Long id);

    ItemRequest add(ItemRequest itemRequest);

    ItemRequest update(Long userId, ItemRequest itemRequest);

    void delete(Long userId, Long id);

    Page<ItemRequest> getExistedForUserId(Long userId, int from, int size);

}
