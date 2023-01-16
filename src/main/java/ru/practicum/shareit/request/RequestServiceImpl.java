package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<ItemRequest> getAllByRequesterId(Long requesterId) {
        checkUserExistence(requesterId);
        return requestRepository.getAllByRequesterIdOrderByCreatedAsc(requesterId);
    }

    @Override
    public ItemRequest getById(Long userId, Long id) {
        checkUserExistence(userId);
        return requestRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Запрос с id=" + id + " несуществует"));
    }

    @Override
    @Transactional
    public ItemRequest add(ItemRequest request) {
        checkUserExistence(request.getRequesterId());
        ItemRequest savedRequest = requestRepository.save(request);
        log.info("Запрос с id={} создан", savedRequest.getId());
        return savedRequest;
    }

    @Override
    public Page<ItemRequest> getExistedForUserId(Long userId, int from, int size) {
        checkUserExistence(userId);

        Pageable pageable = PageRequest.of(from / size, size);
        return requestRepository.getAllByRequesterIdNotOrderByCreatedAsc(userId, pageable);
    }

    private void checkUserExistence(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " несуществует"));
    }
}
