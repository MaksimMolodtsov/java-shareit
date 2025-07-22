package ru.practicum.shareit.request.model;

import jakarta.persistence.*;

/**
 * TODO Sprint add-item-requests.
 */
@Entity
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

}
