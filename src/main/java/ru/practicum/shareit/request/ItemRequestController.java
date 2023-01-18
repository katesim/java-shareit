package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.markers.Create;
import ru.practicum.shareit.request.dto.ItemRequestExtendedDto;
import ru.practicum.shareit.request.dto.ItemRequestDescriptionDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

import static ru.practicum.shareit.common.ShareItConstants.PAGE_SIZE_DEFAULT_TEXT;
import static ru.practicum.shareit.common.ShareItConstants.PAGE_START_FROM_DEFAULT_TEXT;
import static ru.practicum.shareit.common.ShareItConstants.USER_ID_HEADER;


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private final ItemService itemService;

    @PostMapping
    public ItemRequestDto create(
            @RequestHeader(USER_ID_HEADER) long userId,
            @Validated(Create.class) @RequestBody ItemRequestDescriptionDto descriptionDto) {
        ItemRequest request = ItemRequestMapper.toItemRequest(descriptionDto, userId);
        return ItemRequestMapper.toItemRequestDto(itemRequestService.add(request));
    }

    @GetMapping("{id}")
    public ItemRequestExtendedDto getById(
            @RequestHeader(USER_ID_HEADER) long userId,
            @PathVariable long id) {
        ItemRequest request = itemRequestService.getById(userId, id);
        List<Item> responses = itemService.getAllByRequestIdOrderByIdAsc(id);
        return ItemRequestMapper.toItemRequestExtendedDto(request, responses);
    }

    @GetMapping()
    public List<ItemRequestExtendedDto> getAllByRequester(
            @RequestHeader(USER_ID_HEADER) long userId) {
        List<ItemRequest> requests = itemRequestService.getAllByRequesterId(userId);
        List<ItemRequestExtendedDto> requestsDto = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> items = itemService.getAllByRequestIdOrderByIdAsc(request.getId());
            ItemRequestExtendedDto requestDto = ItemRequestMapper.toItemRequestExtendedDto(request, items);
            requestsDto.add(requestDto);
        }
        return requestsDto;
    }

    @GetMapping("all")
    public List<ItemRequestExtendedDto> getAllExisted(
            @RequestHeader(USER_ID_HEADER) long userId,
            @RequestParam(defaultValue = PAGE_START_FROM_DEFAULT_TEXT, required = false) @Min(0) int from,
            @RequestParam(defaultValue = PAGE_SIZE_DEFAULT_TEXT, required = false) @Min(1) int size) {

        Page<ItemRequest> requests = itemRequestService.getExistedForUserId(userId, from, size);

        List<ItemRequestExtendedDto> requestsDto = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> items = itemService.getAllByRequestIdOrderByIdAsc(request.getId());
            ItemRequestExtendedDto requestDto = ItemRequestMapper.toItemRequestExtendedDto(request, items);
            requestsDto.add(requestDto);
        }
        return requestsDto;
    }
}
