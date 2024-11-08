package com.example.roombooking.repository;

import com.example.roombooking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    // Custom query methods (if any) can be defined here
}
