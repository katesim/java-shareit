package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;

import javax.validation.constraints.Min;

import static ru.practicum.shareit.common.ShareItConstants.PAGE_SIZE_DEFAULT_TEXT;
import static ru.practicum.shareit.common.ShareItConstants.PAGE_START_FROM_DEFAULT_TEXT;
import static ru.practicum.shareit.common.ShareItConstants.USER_ID_HEADER;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @Validated(Create.class) @RequestBody ItemRequestDescriptionDto descriptionDto) {
        return client.create(userId, descriptionDto);
    }

    @GetMapping("{id}")
    public  ResponseEntity<Object> getById(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long id) {
        return client.getById(id, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByRequester(
            @RequestHeader(USER_ID_HEADER) long userId) {
        return client.getAllByRequester(userId);
    }

    @GetMapping("all")
    public ResponseEntity<Object> getAllExisted(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = PAGE_START_FROM_DEFAULT_TEXT, required = false) @Min(0) int from,
            @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_TEXT, required = false) @Min(1) int size) {

        return client.getAllExisted(userId, from, size);
    }
}
