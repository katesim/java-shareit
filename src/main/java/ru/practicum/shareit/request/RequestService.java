package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;

import java.util.List;

public interface RequestService {

    List<ItemRequest> getAllByRequesterId(Long requesterId);

    ItemRequest getById(Long userId, Long id);

    ItemRequest add(ItemRequest itemRequest);

    Page<ItemRequest> getExistedForUserId(Long userId, int from, int size);

}
