package com.example.roombooking.controller;

import com.example.roombooking.model.Booking;
import com.example.roombooking.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;  // Import the List class

@RestController
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    // Get all bookings
    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    // Make a new booking
    @PostMapping("/make")
    public Booking makeBooking(@RequestParam Long roomId,
                                @RequestParam String customerName,
                                @RequestParam String checkInDate,
                                @RequestParam String checkOutDate) {
        LocalDate checkIn = LocalDate.parse(checkInDate);
        LocalDate checkOut = LocalDate.parse(checkOutDate);
        return bookingService.makeBooking(roomId, checkIn, checkOut, customerName);
    }
}
