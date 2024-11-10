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
    public Booking makeBooking(Long roomId, LocalDate checkInDate, LocalDate checkOutDate, 
                              String customerName, int numberOfGuests) {
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        
        if (roomOptional.isPresent()) {
            Room room = roomOptional.get();
            // Check if room has sufficient capacity
            if (room.isAvailable() && room.getCapacity() >= numberOfGuests) {
                Booking booking = new Booking();
                booking.setRoom(room);
                booking.setCheckInDate(checkInDate);
                booking.setCheckOutDate(checkOutDate);
                booking.setCustomerName(customerName);
                booking.setNumberOfGuests(numberOfGuests);
                
                // Mark room as unavailable when booked
                room.setAvailable(false);
                
                roomRepository.save(room);
                return bookingRepository.save(booking);
            }
        }
        return null;
    }
}
