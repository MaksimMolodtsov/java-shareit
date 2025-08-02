package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.Named;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemMapper {

    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "request", source = "requestId", qualifiedByName = "idToItemRequest")
    Item toItem(ItemCreateDto itemCreateDto);

    @Named("idToItemRequest")
    default ItemRequest mapIdToItemRequest(Long requestId) {
        if (requestId == null) {
            return null;
        }
        ItemRequest request = new ItemRequest();
        request.setId(requestId);
        return request;
    }

    @Mapping(target = "request", source = "request")
    ItemDto toItemDto(Item item);

    @Mapping(target = "item", ignore = true)
    @Mapping(target = "created", source = "createdAt")
    @Mapping(target = "authorName", source = "user.name")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "itemId", source = "item.id")
    @Mapping(target = "created", source = "createdAt")
    @Mapping(target = "authorName", source = "user.name")
    CommentForItemDto toCommentForItemDto(Comment comment);

    default List<CommentForItemDto> mapComments(List<Comment> comments) {
        if (comments == null) {
            return null;
        }
        return comments.stream()
                .map(this::toCommentForItemDto)
                .collect(Collectors.toList());
    }

}
