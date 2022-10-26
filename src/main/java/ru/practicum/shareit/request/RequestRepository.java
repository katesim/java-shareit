package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RequestRepository extends JpaRepository<ItemRequest, Long> {
    List<ItemRequest> getAllByRequestorIdOrderByCreatedAsc(Long requestorId);

    Page<ItemRequest> getAllByRequestorIdNotOrderByCreatedAsc(Long userId, Pageable pageable);
}
