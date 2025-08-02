package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir FROM ItemRequest ir WHERE ir.user.id = :requestorId ORDER BY ir.created DESC")
    List<ItemRequest> findAllByRequestorIdOrderByCreatedAtDesc(@Param("requestorId") Long requestorId);

    @Query("SELECT r FROM ItemRequest r WHERE r.user.id != :userId ORDER BY r.created DESC")
    List<ItemRequest> findAllOtherUsersRequests(@Param("userId") Long userId);

    @Query("SELECT r FROM ItemRequest r LEFT JOIN FETCH r.items WHERE r.id = :id")
    Optional<ItemRequest> findByIdWithItems(@Param("id") Long id);

}