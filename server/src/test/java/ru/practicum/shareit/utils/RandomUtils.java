package ru.practicum.shareit.utils;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.Random;

public class RandomUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String NAME_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final Random RANDOM = new Random();

    public static UserDto getRandomUser() {
        return UserDto.builder()
                .name(getRandomName(10))
                .email(getRandomEmail())
                .build();
    }

    public static ItemDto getRandomItem(Boolean available) {
        return ItemDto.builder()
                .name(getRandomWord(10))
                .description(getRandomWord(50))
                .available(available)
                .build();
    }

    public static ItemDto getRandomItem() {
        return ItemDto.builder()
                .name(getRandomWord(10))
                .description(getRandomWord(50))
                .available(RANDOM.nextBoolean())
                .build();
    }

    public static BookingDto getBooking(Long itemId, LocalDateTime start, LocalDateTime end) {
        return BookingDto.builder()
                .item(Item.builder().id(itemId).build())
                .start(start)
                .end(end)
                .build();
    }

    public static ItemRequestCreateDto getRandomItemRequest() {
        return ItemRequestCreateDto.builder()
                .description(getRandomWord(20))
                .build();
    }

    public static String getRandomWord(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
        }
        return result.toString();
    }

    public static String getRandomName(int length) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < length; i++) {
            result.append(NAME_CHARACTERS.charAt(RANDOM.nextInt(NAME_CHARACTERS.length())));
        }
        return result.toString();
    }

    public static String getRandomEmail() {
        return getRandomName(3 + RANDOM.nextInt(5))
                + "@"
                + getRandomName(3 + RANDOM.nextInt(5))
                + "."
                + getRandomName(3 + RANDOM.nextInt(5));
    }

}