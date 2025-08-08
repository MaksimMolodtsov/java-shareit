package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import ru.practicum.shareit.booking.model.Booking;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookingMapper {

    Booking toBooking(BookingCreateDto bookingCreateDto);

    @Mapping(target = "booker", source = "user")
    BookingDto toBookingDto(Booking booking);

}