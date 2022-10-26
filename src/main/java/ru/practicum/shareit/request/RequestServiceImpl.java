package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ForbiddenException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<ItemRequest> getAllByRequestorId(Long requestorId) {
        checkUserExistence(requestorId);
        return requestRepository.getAllByRequestorIdOrderByCreatedAsc(requestorId);
    }

    @Override
    public ItemRequest getById(Long id) {
        return requestRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Запрос с id=" + id + " несуществует"));
    }

    @Override
    @Transactional
    public ItemRequest add(ItemRequest request) {
        checkUserExistence(request.getRequestorId());
        ItemRequest savedRequest = requestRepository.save(request);
        log.info("Запрос с id={} создан", savedRequest.getId());
        return savedRequest;
    }

    @Override
    @Transactional
    public ItemRequest update(ItemRequest request) {
        ItemRequest prevRequest = getById(request.getId());

        if (!Objects.equals(request.getRequestorId(), prevRequest.getRequestorId())) {
            throw new ForbiddenException("Изменение запроса доступно только владельцу");
        }
        if (request.getDescription() != null) {
            prevRequest.setDescription(request.getDescription());
        }

        requestRepository.save(prevRequest);
        log.info("Запрос с id={} обновлен", prevRequest.getId());
        return prevRequest;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ItemRequest request = getById(id);
        requestRepository.delete(request);
        log.info("Запрос с id={} удален", id);
    }

    @Override
    public Page<Item> getExistedForUserId(Long userId, Pageable pageable) {
        return null;
    }

    private void checkUserExistence(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException("Пользователь с id=" + userId + " несуществует"));
    }

}
