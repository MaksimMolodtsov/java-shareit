package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ItemRequestMapper {

    ItemRequest toItemRequest(ItemRequestCreateDto itemRequestCreateDto);

    @Mapping(target = "created", source = "created")
    @Mapping(target = "requestor", source = "user")
    @Mapping(target = "items", source = "items")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

}