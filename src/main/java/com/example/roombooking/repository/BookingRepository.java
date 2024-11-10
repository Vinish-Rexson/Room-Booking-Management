package com.example.roombooking.repository;

import com.example.roombooking.model.Booking;
import com.example.roombooking.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByRoomAndCheckOutDateGreaterThanEqualOrderByCheckOutDateAsc(Room room, LocalDate date);
}
