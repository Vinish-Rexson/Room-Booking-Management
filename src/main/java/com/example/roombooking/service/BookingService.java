package com.example.roombooking.service;

import com.example.roombooking.model.Booking;
import com.example.roombooking.model.Room;
import com.example.roombooking.repository.BookingRepository;
import com.example.roombooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private RoomRepository roomRepository;

    // Get all bookings
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // Make a booking
    public Booking makeBooking(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, String customerName) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isPresent() && room.get().isAvailable()) {
            Booking booking = new Booking();
            booking.setRoom(room.get());
            booking.setCheckInDate(checkInDate);
            booking.setCheckOutDate(checkOutDate);
            booking.setCustomerName(customerName);
            room.get().setAvailable(false); // Mark room as unavailable after booking
            roomRepository.save(room.get());
            return bookingRepository.save(booking);
        }
        return null; // Return null if room is not available or not found
    }
}
