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


@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
@Validated
public class ItemRequestController {
    private final RequestService requestService;
    private final ItemService itemService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long userId,
                                 @Validated(Create.class) @RequestBody ItemRequestDescriptionDto descriptionDto) {
        ItemRequest request = ItemRequestMapper.toItemRequest(descriptionDto, userId);
        return ItemRequestMapper.toItemRequestDto(requestService.add(request));
    }

    @GetMapping("{id}")
    public ItemRequestExtendedDto getById(@RequestHeader("X-Sharer-User-Id") long userId,
                                          @PathVariable long id) {
        ItemRequest request = requestService.getById(userId, id);
        List<Item> responses = itemService.getAllByRequestIdOrderByIdAsc(id);
        return ItemRequestMapper.toItemRequestExtendedDto(request, responses);
    }

    @GetMapping()
    public List<ItemRequestExtendedDto> getAllByRequester(@RequestHeader("X-Sharer-User-Id") long userId) {
        List<ItemRequest> requests = requestService.getAllByRequesterId(userId);
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
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(defaultValue = "0", required = false) @Min(0) int from,
            @RequestParam(defaultValue = "10", required = false) @Min(1) int size) {

        Page<ItemRequest> requests = requestService.getExistedForUserId(userId, from, size);

        List<ItemRequestExtendedDto> requestsDto = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<Item> items = itemService.getAllByRequestIdOrderByIdAsc(request.getId());
            ItemRequestExtendedDto requestDto = ItemRequestMapper.toItemRequestExtendedDto(request, items);
            requestsDto.add(requestDto);
        }
        return requestsDto;
    }


}
