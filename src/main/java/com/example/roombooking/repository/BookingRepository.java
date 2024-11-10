package com.example.roombooking.repository;

import com.example.roombooking.model.Booking;
import com.example.roombooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomAndCheckOutDateGreaterThanEqualOrderByCheckOutDateAsc(Room room, LocalDate date);

    @Query("SELECT b FROM Booking b WHERE b.room = :room " +
           "AND b.checkInDate <= :endDate AND b.checkOutDate >= :startDate")
    List<Booking> findByRoomAndDateRange(
            @Param("room") Room room,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
