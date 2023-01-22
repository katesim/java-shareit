package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;

import java.util.List;

public interface ItemRequestService {

    ItemRequest add(ItemRequest itemRequest);

    ItemRequest getById(Long userId, Long id);

    List<ItemRequest> getAllByRequesterId(Long requesterId);

    Page<ItemRequest> getExistedForUserId(Long userId, int from, int size);

}
