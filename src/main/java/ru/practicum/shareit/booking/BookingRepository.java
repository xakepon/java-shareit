package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByItemIdOrderByStartDesc(Long itemId);

    List<Booking> findByItemIdAndBookerIdAndStatusAndEndIsBefore(Long itemId, Long bookerId, BookingStatus status,
                                                                 LocalDateTime endTime);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId, LocalDateTime startTime,
                                                                              LocalDateTime endTime);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long userId, LocalDateTime time);

    List<Booking> findByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(Long bookerId, LocalDateTime time,
                                                                         BookingStatus status);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId);

    List<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId, LocalDateTime start,
                                                                                 LocalDateTime end);

    List<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId, LocalDateTime time);

    List<Booking> findByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(Long ownerId, LocalDateTime time,
                                                                            BookingStatus status);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId, BookingStatus status);

}
