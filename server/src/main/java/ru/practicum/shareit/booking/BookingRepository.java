package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    List<Booking> findByItemIdOrderByStartDesc(Long itemId);


    List<Booking> findByItemIdAndBookerIdAndStatusAndEndIsBefore(Long itemId, Long bookerId,
                                                                 BookingStatus status,
                                                                 LocalDateTime endTime);

    Page<Booking> findByBookerIdOrderByStartDesc(Long bookerId,
                                                 Pageable pageRequest);

    Page<Booking> findByBookerIdAndEndIsAfterAndStartIsBeforeOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime startTime,
                                                                              LocalDateTime endTime,
                                                                              Pageable pageRequest);

    Page<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId,
                                                               LocalDateTime startTime,
                                                               Pageable pageRequest);

    Page<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long userId,
                                                                LocalDateTime endTime,
                                                                Pageable pageRequest);

    Page<Booking> findByBookerIdAndStartIsAfterAndStatusOrderByStartDesc(Long bookerId,
                                                                         LocalDateTime time,
                                                                         BookingStatus status,
                                                                         Pageable pageRequest);

    Page<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId,
                                                          BookingStatus status,
                                                          Pageable pageRequest);

    Page<Booking> findByItemOwnerIdOrderByStartDesc(Long ownerId,
                                                    Pageable pageRequest);

    Page<Booking> findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long ownerId,
                                                                                 LocalDateTime start,
                                                                                 LocalDateTime end,
                                                                                 Pageable pageRequest);

    Page<Booking> findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(Long ownerId,
                                                                  LocalDateTime time,
                                                                  Pageable pageRequest);

    Page<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long ownerId,
                                                                   LocalDateTime time,
                                                                   Pageable pageRequest);

    Page<Booking> findByItemOwnerIdAndStartIsAfterAndStatusOrderByStartDesc(Long ownerId,
                                                                            LocalDateTime time,
                                                                            BookingStatus status,
                                                                            Pageable pageRequest);

    Page<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long ownerId,
                                                             BookingStatus status,
                                                             Pageable pageRequest);

}
