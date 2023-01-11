package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemExtendedDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;


public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                item.getRequestId()
        );
    }

    public static ItemExtendedDto toItemExtendedDto(Item item) {
        return new ItemExtendedDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                null,
                null,
                null,
                item.getRequestId()
        );
    }

    public static ItemExtendedDto.CommentDto toCommentDto(Comment comment, User author) {
        return new ItemExtendedDto.CommentDto(
                comment.getId(),
                comment.getText(),
                author.getName(),
                comment.getCreated()
        );
    }

    public static Comment toComment(Long itemId, Long authorId, CommentRequestDto commentDto) {
        Comment comment = new Comment();
        comment.setItemId(itemId);
        comment.setAuthorId(authorId);
        comment.setText(commentDto.getText());
        comment.setCreated(LocalDateTime.now());
        return comment;
    }


    public static Item toItem(ItemDto itemDto, Long ownerId) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        item.setOwnerId(ownerId);
        item.setRequestId(itemDto.getRequestId());
        return item;
    }
}
